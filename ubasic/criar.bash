echo "cria virtual machine..."
cd  vm
javac VMRun.java 

echo "cria compilador..."
cd ..
java -jar cocor/Coco.jar -frames cocor/ microc.atg 
javac *.java

