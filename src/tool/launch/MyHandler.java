package tool.launch;

import soot.jimple.infoflow.handlers.ResultsAvailableHandler;
import soot.jimple.infoflow.results.DataFlowResult;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.results.ResultSinkInfo;
import soot.jimple.infoflow.results.ResultSourceInfo;
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;
import soot.util.MultiMap;

import java.util.Set;

//todo: implement ResultsAvailableHandler to print more information about the results
public class MyHandler implements ResultsAvailableHandler {
    @Override
    public void onResultsAvailable(IInfoflowCFG cfg, InfoflowResults results) {
        if (results != null && !results.isEmpty()) {
            System.out.println("results.size(): " + results.size());

            Set<DataFlowResult> resultSet = results.getResultSet();
            System.out.println("resultsSet.size(): " + resultSet.size());
            int path = 0;
            for (DataFlowResult result : resultSet) {
                System.out.println("path: " + ++path);
                System.out.println("source: " + result.getSource() + " -> sink: " + result.getSink());
            }

            MultiMap<ResultSinkInfo, ResultSourceInfo> sinkMapSources = results.getResults();
            int sinks = sinkMapSources.size();

            System.out.println("results.getResults().size(): " + sinkMapSources.size());
            System.out.println("sinks: " + sinks);

            path = 0;
            for (ResultSinkInfo resultSinkInfo : sinkMapSources.keySet()) {
                Set<ResultSourceInfo> resultSourceInfos = sinkMapSources.get(resultSinkInfo);
                for (ResultSourceInfo resultSourceInfo : resultSourceInfos) {
                    System.out.println("path: " + ++path);
                    System.out.println("source: " + resultSourceInfo + " -> sink: " + resultSinkInfo);
                }
            }

        } else {
            System.out.println("No results found! Report from MyHandler");
        }
    }
}
