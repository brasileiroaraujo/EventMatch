package DbpediaApi;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

public class DbPediaAPI {
	public static void main(String[] args) {
		getDBpediaInfo();
	}
	
	
	public static void getDBpediaInfo() { 
		String query1 = " 	PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ " PREFIX r: <http://dbpedia.org/resource/> "
				+ " PREFIX d: <http://dbpedia.org/ontology/> "
				+ " PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
				+ " SELECT ?label "
				+ " WHERE "
				+ " { "
				+ " <http://dbpedia.org/resource/Sport_Club_Corinthians_Paulista> foaf:nick ?label "
//				+ " ?album d:producer :Timbaland . "
//				+ " ?album d:musicalArtist ?artist ."
//				+ " ?album rdfs:label ?albumName . "
//				+ " ?artist rdfs:label ?artistName ."
//				+ " FILTER ( lang(?label) = \"en\")"
//				+ " FILTER ( lang(?label) = \"pt\" )" 
				+ " }";
		 
//		 String query1 = "SELECT distinct ?label" + 
//		 		"WHERE {" + 
//		 		"    <http://dbpedia.org/resource/Sport_Club_Corinthians_Paulista>" + 
//		 		"        foaf:nick ?label" + 
//		 		"    FILTER (lang(?label) = 'en')" + 
//		 		"}";

		ResultSet results = SPARQLUtil.INSTANCE.dbpediaQuery(query1);
		ResultSetFormatter.out(System.out, results);

		while (results.hasNext()) {

			QuerySolution soln = results.next();

			Literal albumName = soln.getLiteral("label");
			
			System.out.println(albumName);

		}
	}
}
