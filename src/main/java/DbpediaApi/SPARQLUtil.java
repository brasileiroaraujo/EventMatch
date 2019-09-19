package DbpediaApi;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.util.FileManager;

public enum SPARQLUtil {

	INSTANCE;
	
	public static void printQuery(String queryString, String inputFileName)
	{
		 
		Model model = loadRDF(inputFileName);
		
		Query query = QueryFactory.create(queryString); 
        
		QueryExecution queryExecution = QueryExecutionFactory.create(query,model);
        
        ResultSet results = queryExecution.execSelect();
        
        ResultSetFormatter.out(System.out, results, query) ;
        
        queryExecution.close();
	}
	
	public static void printQueryParameterizedString(String queryString, String inputFileName, String parameter, String value)
	{
		 
		Model model = loadRDF(inputFileName);
		
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(queryString);
		
		queryStr.setLiteral(parameter, value);
		
		Query query = QueryFactory.create(queryStr.toString()); 
        
		QueryExecution queryExecution = QueryExecutionFactory.create(query,model);
        
        ResultSet results = queryExecution.execSelect();
        
        ResultSetFormatter.out(System.out, results) ;
        
        queryExecution.close();
	}
	
	
	public static void printQueryMAP(String queryString, String inputFileName, String parameter, String value)
	{
		 
		Model model = loadRDF(inputFileName);
		
		RDFNode node = model.createResource(value);
		
		QuerySolutionMap map = new QuerySolutionMap();
		
		map.add(parameter, node);
		
		Query query = QueryFactory.create(queryString); 
        
		QueryExecution queryExecution = QueryExecutionFactory.create(query,model, map);
        
        ResultSet results = queryExecution.execSelect();
        
        ResultSetFormatter.out(System.out, results) ;
     
        queryExecution.close();
     
	}
	
	
	private static Model loadRDF(String inputFileName) {
		// create an empty model
		Model model = ModelFactory.createDefaultModel();

		// use the FileManager to find the input file
		InputStream in = FileManager.get().open(inputFileName);

		if (in == null) {
			throw new IllegalArgumentException("File: " + inputFileName
					+ " not found");
		}

		// read the RDF/XML file
		model.read(new InputStreamReader(in), "");
		
		return model;
	}
	
	public static  ResultSet dbpediaQuery (String query)
	{
		QueryExecution queryExecution = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
		
		ResultSet results = queryExecution.execSelect();
		
		return results;
	}
	
	public static  ResultSet externalQuery (String query, String serviceURL)
	{
		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(serviceURL, query);
		
		ResultSet results = queryExecution.execSelect();
		
		return results;
	}
	
}
