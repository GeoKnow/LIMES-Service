package org.linkeddata.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class QueryChunks {

    private static final Logger log = Logger.getLogger(QueryChunks.class);

    static int linesLimit = 1000;

    public static List<String> generateInsertChunks(String graph, Model model, String uriBase)
            throws IOException {

        List<String> queries = new ArrayList<String>();

        // generate queries of 100 lines each
        StmtIterator stmts = model.listStatements();

        log.info("Generating chunks for " + model.size() + " triples");

        int linesCount = 0, total = 0;
        HashMap<String, String> blancNodes = new HashMap<String, String>();

        Model tmpModel = ModelFactory.createDefaultModel();

        while (stmts.hasNext()) {

            if (linesCount < linesLimit) {

                Statement stmt = stmts.next();
                Resource subject = null;
                RDFNode object = null;
                // find bnodes to skolemise them
                if (stmt.getSubject().isAnon()) {
                    String oldBN = stmt.getSubject().asNode().getBlankNodeLabel();
                    if (blancNodes.containsKey(oldBN)) {
                        subject = tmpModel.getResource(blancNodes.get(oldBN));
                    } else {
                        String newBN = uriBase + "bnode#" + UUID.randomUUID();
                        blancNodes.put(oldBN, newBN);
                        subject = tmpModel.createResource(newBN);
                    }
                } else
                    subject = stmt.getSubject();

                if (stmt.getObject().isAnon()) {
                    String oldBN = stmt.getObject().asNode().getBlankNodeLabel();
                    if (blancNodes.containsKey(oldBN)) {
                        object = tmpModel.getResource(blancNodes.get(oldBN));
                    } else {
                        String newBN = uriBase + "bnode#" + UUID.randomUUID();
                        blancNodes.put(oldBN, newBN);
                        object = tmpModel.createResource(newBN);
                    }
                } else
                    object = stmt.getObject();

                tmpModel.add(subject, stmt.getPredicate(), object);
                linesCount++;
            } else {
                queries.add(writeInsertQuery(tmpModel, graph));
                total += linesCount;
                linesCount = 0;
                tmpModel.removeAll();
            }
        }

        if (!tmpModel.isEmpty()) {
            queries.add(writeInsertQuery(tmpModel, graph));
            total += linesCount;
        }

        log.info(total + " generated sumcheck in " + queries.size() + " queries");

        return queries;
    }

    private static String writeInsertQuery(Model m, String graph) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        String queryString;
        m.write(os, "TURTLE");
        if (graph != "")
            queryString = " INSERT INTO <" + graph + "> { " + os.toString("UTF-8") + "}";
        else
            queryString = "INSERT { " + os.toString("UTF-8") + "}";
        os.close();
        return queryString;

    }
}
