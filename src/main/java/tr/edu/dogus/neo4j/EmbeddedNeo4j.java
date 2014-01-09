package tr.edu.dogus.neo4j;

import java.io.File;
import java.io.IOException;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.util.FileUtils;
import org.neo4j.tooling.GlobalGraphOperations;

import tr.edu.dogus.neo4j.db.MysqlConnector;
import tr.edu.dogus.neo4j.db.TwitterUser;

public class EmbeddedNeo4j {

	private static final String DB_PATH = "target/neo4j-hello-db";

	public String greeting;

	// START SNIPPET: vars
	GraphDatabaseService graphDb;

	// END SNIPPET: vars

	// START SNIPPET: createReltype
	private static enum RelTypes implements RelationshipType {

		Friend, Follower
	}

	// END SNIPPET: createReltype

	public static void main(final String[] args) {
		EmbeddedNeo4j neo = new EmbeddedNeo4j();
		neo.createDb();

		neo.removeData();
		neo.shutDown();
	}

	void createDb() {
		MysqlConnector myConnector = new MysqlConnector();

		clearDb();
		// START SNIPPET: startDb
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);

		// END SNIPPET: startDb

		// START SNIPPET: transaction
		Transaction tx;
		try {
			tx = graphDb.beginTx();

			// Database operations go here
			// END SNIPPET: transaction
			// START SNIPPET: addData
			for (TwitterUser twitterUser : myConnector.getAllTwitterUser()) {
				Node node = graphDb.createNode();
				node.setProperty("name", twitterUser.getName());
				node.setProperty("user_id", twitterUser.getUserId());
				node.setProperty("twitter_user_id", twitterUser.getTwitterUserId());
			}

			// Relationship relationship = firstNode.createRelationshipTo(secondNode, RelTypes.KNOWS);
			// END SNIPPET: addData
			// START SNIPPET: transaction
			tx.success();

			for (Node node : GlobalGraphOperations.at(graphDb).getAllNodes()) {
				if (node.hasProperty("name")) {
					System.out.println(node.getProperty("name"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// END SNIPPET: transaction
	}

	private void clearDb() {
		try {
			FileUtils.deleteRecursively(new File(DB_PATH));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	void removeData() {
		Transaction tx;
		try {
			tx = graphDb.beginTx();
			// START SNIPPET: removingData
			// let's remove the data
			for (Node node : graphDb.getAllNodes()) {
				node.delete();
			}
			// END SNIPPET: removingData

			tx.success();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void shutDown() {
		System.out.println();
		System.out.println("Shutting down database ...");
		// START SNIPPET: shutdownServer
		graphDb.shutdown();
		// END SNIPPET: shutdownServer
	}

	// END SNIPPET: shutdownHook
}
