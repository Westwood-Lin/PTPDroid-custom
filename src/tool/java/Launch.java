package tool.java;

import org.xmlpull.v1.XmlPullParserException;
import soot.jimple.toolkits.callgraph.CallGraph;
import tool.analysis.infoflowResult;
import tool.consistency.ConsistencyAnalysis;
import tool.consistency.ConsistencyAnalysisResult;
import tool.mapping.apiMappingToPrivacy;
import tool.mapping.apkMappingToEntity;
import tool.mapping.ipMappingToDNS;
import tool.mapping.urlMappingToEntity;
import tool.modifyToTuple.modifyFlowResults;
import tool.modifyToTuple.modifyPolicyResults;
import tool.ontology.EntityOntologyMap;
import tool.ontology.PrivacyOntologyMap;
import tool.other.enhance;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class Launch {

    public static String apkPath;

//    public static String projectBaseDirPath;

    public static String jarsPath;
    public static String configPath; // = projectBaseDirPath + File.separator + "configs";
    public static String flowDroidConfigs; // = configPath + File.separator + "FlowDroidConfigs";
    public static String androidCallbackPath; // = flowDroidConfigs + File.separator + "AndroidCallBacks.txt";
    public static String sourceAndSinkPath; // = flowDroidConfigs + File.separator + "SourceAndSinks.txt";
    public static String easyTaintWrapperSource; // = flowDroidConfigs + File.separator + "EasyTaintWrapperSource.txt";

    //闅愮绛栫暐鐨勫垎鏋愮粨鏋�
    public static String privacyPolicyResults; // = configPath + File.separator + "policyResults.txt";

    //api鏂规硶鍜岄殣绉佷俊鎭殑鏄犲皠  apiMappingToPrivacy.txt
    public static String apiTOPrivacy; // = configPath + File.separator + "apiMappingToPrivacy.txt";
    //闅愮淇℃伅鏈綋  privacyOntology.txt
    public static String privacyTOOntology; // = configPath + File.separator + "privacyOntology.txt";
    //绗笁鏂瑰疄浣撶殑鏈綋 entityOntology.txt
    public static String EntityOntology; // = configPath + File.separator + "entityOntology.txt";
    //ip鍦板潃鍒皍rl鐨勬槧灏�  ipMappingToDNS.txt
    public static String IPToDNS;// = configPath + File.separator + "ipMappingToDNS.txt";
    //dns鍩熷悕鍒板疄浣撶殑鏄犲皠  DNSMappingToEntity.txt
    public static String DNSToEntity; // = configPath + File.separator + "DNSMappingToEntity.txt";

    public static String firstParty;
    public static Set<String> missingThirdParty = new HashSet<>();

    private static void localTest() throws XmlPullParserException, IOException {
//        apkPath = "C:\\Users\\Yalin Feng\\Code\\AndroidPrivacy\\Project\\data\\apk\\notepad.apk";
//        configPath="C:\\Users\\Yalin Feng\\Code\\AndroidPrivacy\\Project\\configs";
//        jarsPath = "C:\\Users\\Yalin Feng\\Code\\AndroidPrivacy\\Project\\libs\\platforms";
        apkPath = "/home/fyl/project/code/AndroPrivacyConsistency/data/apk/RQ1/ap-news_5.17(10011).apk";
        configPath = "/home/fyl/project/code/PTPDroid/configs";
        jarsPath = "/home/fyl/project/code/AndroPrivacyConsistency/libs/platforms/";
        updatePaths(configPath);
        run(new String[]{});
    }

    public static void updatePaths(String config) {
        configPath = config;
        flowDroidConfigs = configPath + File.separator + "FlowDroidConfigs";
        androidCallbackPath = flowDroidConfigs + File.separator + "AndroidCallBacks.txt";
        sourceAndSinkPath = flowDroidConfigs + File.separator + "SourceAndSinks.txt";
        easyTaintWrapperSource = flowDroidConfigs + File.separator + "EasyTaintWrapperSource.txt";
        privacyPolicyResults = configPath + File.separator + "policyResults.txt";
        apiTOPrivacy = configPath + File.separator + "apiMappingToPrivacy.txt";
        privacyTOOntology = configPath + File.separator + "privacyOntology.txt";
        EntityOntology = configPath + File.separator + "entityOntology.txt";
        IPToDNS = configPath + File.separator + "ipMappingToDNS.txt";
        DNSToEntity = configPath + File.separator + "DNSMappingToEntity.txt";
    }

    public static void run(String[] args) throws IOException, XmlPullParserException {
        long start = System.currentTimeMillis();
        //鍒濆鍖杘ntology鍜宮apping
        EntityOntologyMap.initEntity(EntityOntology);
        PrivacyOntologyMap.initPrivacy(privacyTOOntology);
        ipMappingToDNS.initIp(IPToDNS);
        urlMappingToEntity.init(DNSToEntity);
        apiMappingToPrivacy.initApi(apiTOPrivacy);

        //灏嗛殣绉佺瓥鐣ョ殑鍒嗘瀽缁撴灉杞寲鎴愯鑼冩牸寮�
        List<String[]> policyResults = modifyPolicyResults.modifiedPolicyResults(privacyPolicyResults);

        //姹＄偣鍒嗘瀽鎵�闇�鐨勯厤缃�
        String[] config = new String[]{apkPath, jarsPath, androidCallbackPath, sourceAndSinkPath, easyTaintWrapperSource};

        CallGraph callGraph = infoflowResult.getCallGraph(config);
        firstParty = apkMappingToEntity.changeToEntity(apkPath);

        List<String[]> formedResults = new LinkedList<>();
        List<String[]> sinksToEntity = new LinkedList<>();
        boolean flag = true;
        try {
            List<String[]> res = infoflowResult.taintAnalysis(config);
            List<String[]> sinksToEntity1 = modifyFlowResults.findThirdPartyName(res, callGraph);
            sinksToEntity = sinksToEntity1;
            modifyFlowResults.union(config, sinksToEntity1);
            List<String[]> formedResults1 = modifyFlowResults.modifiedFlowResults(sinksToEntity1, firstParty);
            formedResults = formedResults1;
        } catch (Exception e) {
            flag = false;
            List<String[]> res = tool.other.main.sootAnalysis(config);
        }

        if (flag) {
            System.out.println("**************************");
            System.out.println("闈欐�佸垎鏋愮粨鏋滐細");
            Map<String, Set<String>> results = modifyFlowResults.modifyResultStructure(formedResults);
            Iterator<Map.Entry<String, Set<String>>> iterator = results.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Set<String>> entry = iterator.next();
                System.out.print(entry.getKey() + " : ");
                for (String data : entry.getValue()) {
                    if (data.contains("<") || data.contains("(")) {
                    } else {
                        System.out.print(data + " , ");
                    }
                }
                System.out.println();
            }
            System.out.println("**************************");
        }


        long end = System.currentTimeMillis();
        long time = end - start;
        double timeInMinutes = time / 60000.0;
        System.out.printf("Execution time: %.4f minutes%n", timeInMinutes);

        List<String[]> flowResults = modifyFlowResults.modifiedFlowResults(sinksToEntity, firstParty);
        List<String[]> appResults = enhance.modify(flowResults);
        ConsistencyAnalysisResult result = ConsistencyAnalysis.consistencyAnalysis(appResults, policyResults);
        result.outputInconsistentResults(result);

    }


    public static void main(String[] args) throws IOException, XmlPullParserException, URISyntaxException {
        try {
            if (args != null && args.length > 0) {
                System.out.println("args: " + Arrays.toString(args));

                configPath = "/home/fyl/project/code/PTPDroid/configs";
                jarsPath = "/home/fyl/project/code/AndroPrivacyConsistency/libs/platforms";
                System.out.println("configPath: " + configPath);
                System.out.println("jarsPath: " + jarsPath);

                updatePaths(configPath);

                File file = new File(args[0]);
                if (file.exists()) {
                    System.out.println("File exists: " + args[0]);
                    System.out.println("[Start]");
                    apkPath = args[0];
                    run(new String[]{Arrays.toString(args)});
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
            localTest();
        }
    }
}