package tr.edu.dogus.neo4j;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.kernel.impl.util.FileUtils;
import org.neo4j.tooling.GlobalGraphOperations;

import tr.edu.dogus.neo4j.db.MysqlConnector;
import tr.edu.dogus.neo4j.db.TwitterUser;

public class EmbeddedNeo4j {

	private static final String DB_PATH = "target/neo4j-hello-db";

	public String greeting;

	GraphDatabaseService graphDb;

	private static enum RelTypes implements RelationshipType {
		Friend, Follower
	}

	public static void main(final String[] args) {
		EmbeddedNeo4j neo = new EmbeddedNeo4j();
		neo.createDb();

		neo.operations();

		neo.removeData();
		neo.shutDown();
	}

	private void operations() {
		ExecutionEngine engine = new ExecutionEngine(graphDb);

		ExecutionResult result;
		try (Transaction ignored = graphDb.beginTx()) {
			// adi `ya` ile baslayan tum nodelari bul
			result = engine.execute("start n=node(*) where n.name =~ 'ya.*' return n, n.name");

			// geri donen datalardan bize `n` lazim sadece, node datasi aslinda o

			Iterator<Node> n_column = result.columnAs("n");
			for (Node node : IteratorUtil.asIterable(n_column)) {
				// note: we're grabbing the name property from the node, not from the n.name in this case.
				String nodeResult = node + ": " + node.getProperty("name");
				System.out.println(nodeResult);
			}
		}
	}

	void createDb() {
		MysqlConnector myConnector = new MysqlConnector();

		clearDb();

		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);

		Transaction tx;
		try {
			tx = graphDb.beginTx();

			for (TwitterUser twitterUser : myConnector.getAllTwitterUser()) {
				// check if any data is null or not
				if (twitterUser.getName() == null || twitterUser.getUserId() == null
						|| twitterUser.getTwitterUserId() == null) {
					continue;
				}

				Node node = graphDb.createNode();
				node.setProperty("name", twitterUser.getName());
				node.setProperty("user_id", twitterUser.getUserId());
				node.setProperty("twitter_user_id", twitterUser.getTwitterUserId());
			}

			// Relationship relationship = firstNode.createRelationshipTo(secondNode, RelTypes.KNOWS);
			tx.success();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			// let's remove the data
			for (Node node : GlobalGraphOperations.at(graphDb).getAllNodes()) {
				node.delete();
			}

			tx.success();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void shutDown() {
		System.out.println();
		System.out.println("Shutting down database ...");
		graphDb.shutdown();
	}

	// END SNIPPET: shutdownHook
}
