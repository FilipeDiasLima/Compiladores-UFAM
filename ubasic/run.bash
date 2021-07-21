FILE=$1
echo "compila "${FILE}".uc"
java Compile ${FILE}.uc 

echo "executa o codigo objeto "${FILE}".obj"
java -cp vm/ VMRun ${FILE}.obj 
