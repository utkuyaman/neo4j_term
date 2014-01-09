package tr.edu.dogus.neo4j;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

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

	private static Properties props = new Properties();

	GraphDatabaseService graphDb;

	MysqlConnector myConnector;

	private static enum RelTypes implements RelationshipType {
		Friend, Follower
	}

	public EmbeddedNeo4j() {
	}

	public Boolean init() {
		try {
			props.load(this.getClass().getResourceAsStream("/config.properties"));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		myConnector = new MysqlConnector(props.getProperty("db.host"), props.getProperty("db.name"),
				props.getProperty("db.user"), props.getProperty("db.pass"));

		return true;
	}

	public void operations() {
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
