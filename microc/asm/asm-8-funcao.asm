/*
int par(int n)
{
	if (n%2 == 0) return 1; else return 0;
}
*/

_par_:
	enter 1 1
	load 0
	const 2
	rem
	const 0
	jne ELSE_PAR 
	const 1
	jmp END_PAR
ELSE_PAR:
	const 0
END_PAR:
	exit
	return

/*

void main()
{
	int num; printf("Entre com um numero: "); scanf(num);
	if (par(num) == 1)
		printf(num, "eh par\n");
	else
		printf(num, "eh impar\n");
}

*/

_main_:
	enter 0 1
	prints "Entre com um numero: " 
	scani
	store 0
	load 0
	call _par_
	const 1
	jne ELSE_main
	load 0
	printi
	prints " eh par\n"
	jmp END_main
ELSE_main:
	load 0
	printi
	prints " eh impar\n"
END_main:
	exit
	return

