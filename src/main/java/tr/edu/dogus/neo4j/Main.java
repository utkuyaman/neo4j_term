package tr.edu.dogus.neo4j;

public class Main {

	public static void main(String[] args) {
		EmbeddedNeo4j neo = new EmbeddedNeo4j();
		if (!neo.init()) {
			return;
		}

		neo.createDb();

		neo.operations();

		neo.removeData();
		neo.shutDown();
	}

}
