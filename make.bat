@echo on 
del bin\com\mit\achavda\iphistory\*.class
cd src
javac com\mit\achavda\iphistory\*.java -d ..\bin -cp ..\lib\opencsv.jar;..\lib\domaintoolsapi.jar;..\lib\jaxp-api.jar
cd ..
java -cp .\bin;.\lib\opencsv.jar;.\lib\domaintoolsapi.jar;.\lib\jaxp-api.jar com.mit.achavda.iphistory.Main  
