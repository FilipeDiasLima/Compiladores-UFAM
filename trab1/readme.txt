Tradutor de código microC para Python

Para executar:

1 - PASSO:
java -jar cocor/Coco.jar -frames cocor uc-ts.atg

2 - PASSO:
javac *.java

3 - PASSO:
java Compile fontes/prog1.uc

lembrando que 'fontes/prog1.uc' é o diretorio e o nome do arquivo microC

BREVE EXPLICAÇÃO:

Assume-se que o "espaço externo" é a parte Global do código;
A parte global é composta por Funções;
As funções geram um Bloco;
E um bloco é composto por várias Instruções;

Global:
  Funcao:
    Bloco:
      Instrução

  Funcao:
    Bloco:
      Instrução

  main()

Para resolver o erro de bloco, já que ao abrir um "{" deveria abrir-se um bloco,
então é adicionado um contador global "cont" para contar a quantidade de "{" que foram
abertas e cada vez que ele passar por uma "}" ele decrementa 1 de "cont", fazendo assim,
cada "{" adicionar uma identação(já que a identação marca o que pertence a um bloco).

Assume-se também que, a única instrução fora de um bloco, será declaração de Variáveis
Globais, criando assim uma classe "Class _global_", contendo todos as variáveis globais.


░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
░░░░░░░░░░░░░▄▄▄▄▄▄▄░░░░░░░░░
░░░░░░░░░▄▀▀▀░░░░░░░▀▄░░░░░░░
░░░░░░░▄▀░░░░░░░░░░░░▀▄░░░░░░
░░░░░░▄▀░░░░░░░░░░▄▀▀▄▀▄░░░░░
░░░░▄▀░░░░░░░░░░▄▀░░██▄▀▄░░░░
░░░▄▀░░▄▀▀▀▄░░░░█░░░▀▀░█▀▄░░░
░░░█░░█▄▄░░░█░░░▀▄░░░░░▐░█░░░
░░▐▌░░█▀▀░░▄▀░░░░░▀▄▄▄▄▀░░█░░
░░▐▌░░█░░░▄▀░░░░░░░░░░░░░░█░░
░░▐▌░░░▀▀▀░░░░░░░░░░░░░░░░▐▌░
░░▐▌░░░░░░░░░░░░░░░▄░░░░░░▐▌░
░░▐▌░░░░░░░░░▄░░░░░█░░░░░░▐▌░
░░░█░░░░░░░░░▀█▄░░▄█░░░░░░▐▌░
░░░▐▌░░░░░░░░░░▀▀▀▀░░░░░░░▐▌░
░░░░█░░░░░░░░░░░░░░░░░░░░░█░░
░░░░▐▌▀▄░░░░░░░░░░░░░░░░░▐▌░░
░░░░░█░░▀░░░░░░░░░░░░░░░░▀░░░
░░░░░░░░░░░░░░░░░░░░░░░░░░░░░