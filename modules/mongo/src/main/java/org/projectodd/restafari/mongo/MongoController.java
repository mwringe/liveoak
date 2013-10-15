package org.projectodd.restafari.mongo;

import com.mongodb.*;
import org.bson.types.ObjectId;
import org.projectodd.restafari.container.SimpleObjectResource;
import org.projectodd.restafari.spi.Config;
import org.projectodd.restafari.spi.ControllerContext;
import org.projectodd.restafari.spi.InitializationException;
import org.projectodd.restafari.spi.Pagination;
import org.projectodd.restafari.spi.RequestContext;
import org.projectodd.restafari.spi.Resource;
import org.projectodd.restafari.spi.ResourceController;
import org.projectodd.restafari.spi.Responder;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class MongoController implements ResourceController {

    private MongoClient mongo;
    private DB db;

    @Override
    public void initialize(ControllerContext context) throws InitializationException {
        Config config = context.getConfig();
        String host = config.get("host", "localhost");
        int port = config.get("port", 27017);
        String dbName = config.getRequired("db");

        try {
            mongo = new MongoClient(host, port);
            db = mongo.getDB(dbName);
            if (db == null) {
                throw new InitializationException("Unknown database " + dbName);
            }
        } catch (UnknownHostException e) {
            throw new InitializationException("Could not handleConnect to " + host + " on port " + port);
        }
    }

    @Override
    public void destroy() {
        if (mongo != null) {
            mongo.close();
        }
    }

    @Override
    public void getResource(RequestContext context, String collectionName, String id, Responder responder) {
        forCollection(collectionName, responder, collection -> {
            DBObject dbObject = collection.findOne(new BasicDBObject("_id", new ObjectId(id)));
            if (dbObject == null) {
                responder.noSuchResource(id);
            } else {
                responder.resource(new DBObjectResource(dbObject));
            }
        });
    }

    @Override
    public void getResources(RequestContext context, String collectionName, Pagination pagination, Responder responder) {
        forCollection(collectionName, responder, collection -> {
            DBCursor cursor = collection.find();
            if (pagination != null) {
                if (pagination.getLimit() > 0) {
                    cursor.limit(pagination.getLimit());
                }
                if (pagination.getOffset() > 0) {
                    cursor.skip(pagination.getOffset());
                }
            }
            Collection<Resource> resources = new ArrayList<>(cursor.size());
            while (cursor.hasNext()) {
                resources.add(new DBObjectResource(cursor.next()));
            }

            responder.resources(resources);
        });
    }

    @Override
    public void createResource(RequestContext context, String collectionName, Resource resource, Responder responder) {
        DBCollection collection = db.getCollection(collectionName);
        SimpleObjectResource sor = (SimpleObjectResource)  resource;
        BasicDBObject object = new BasicDBObject();

        for (String prop : sor.getPropertyNames())     {
            object.append(prop, sor.getProperty(prop));
        }

        // Note: insert modifies the object being inserted
        collection.insert(object);
        // after insert the object is now the same as it appears in the db
        responder.resourceCreated(new DBObjectResource(object));
    }

    @Override
    public void updateResource(RequestContext context, String collectionName, String id, Resource resource, Responder responder) {
        DBCollection collection = db.getCollection(collectionName);

        DBObject object = collection.findOne(new BasicDBObject("_id", new ObjectId(id)));

        SimpleObjectResource sor = (SimpleObjectResource)  resource;

        for (String prop : sor.getPropertyNames()) {
            object.put(prop, sor.getProperty(prop));
        }

        collection.save(object);

        responder.resourceUpdated(new DBObjectResource(object));
    }

    @Override
    public void deleteResource(RequestContext context, String collectionName, String id, Responder responder) {
        DBCollection collection = db.getCollection(collectionName);

        BasicDBObject object = new BasicDBObject();
        object.append("_id", new ObjectId(id));

        DBObject actualObject = collection.findOne(object);
        collection.remove(actualObject);

        //TODO: figure out what, if anything, should actually be returned here. Its probably best to indicate this
        // with just a status code.
        responder.resourceDeleted(new DBObjectResource(object));
    }

    private void forCollection(String name, Responder responder, Consumer<DBCollection> consumer) {
        DBCollection collection = db.getCollection(name);
        if (collection == null) {
            responder.noSuchCollection(name);
        }

        consumer.accept(collection);
    }
}
