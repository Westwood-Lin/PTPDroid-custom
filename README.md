# Fork README
原来的 PTPDroid 仓库是 ICSE'23 中一份论文 PTPDroid: Detecting Violated User Privacy Disclosures to Third-Parties of Android Apps 的实现。
其中 `main.java` 有许多硬编码的路径，不方便使用，我添加了 Launch.java, 简化了路径和依赖配置。
现在需要配置的路径数从十多个减少到了2个。

同时，为了便于查看结果，我添加了一个Handler查看静态分析结果。

最后，为了便于用 Maven 打包，我添加了一个 maven-assembly-plugin 插件。

原项目 REPOSITORY: https://github.com/wsong-nj/PTPDroid

# Origin README
PTPDroid is an Eclipse project. Then please follow the following three steps to make the code run:

1.Import the project first: After openning Eclipse, please click: ①File ②Import ③General ④Existing Projects into Workspace; You need to add external JARS from ./libs/.

2.Modify parameters: Please open Java file "tool.java.Launch" in the PTPDroid to modify the variables "apkPath" and "jarsPath" to your own. "jarspath" refers to the Android sdk platforms folder, and "apkpath" refers to the path of the apks(apps) to be analyzed. Other configurations can be found in the file "configs", which are related to the ontologies and mappings.

3.Run: Run tool.java.Launch, and after a while the results that which third-parties collect which data types will be generated. If you want to get the consistency results between the app code and the privacy policy. You can put the results of PolicyLint (https://github.com/benandow/PrivacyPolicyAnalysis) in the variables "privacyPolicyResults". The output classfied flow results into four types of disclosures: clear disclosure, vague disclosures, omitted disclosures, and incorrect disclosures.
