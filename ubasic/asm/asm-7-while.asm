_main_:
	enter 0 1
	const 10
	store 0
WHILE:
	load 0
	const 0
	jle END_WHILE
	prints "n = " 		/* printf("n = ", n, "\n") */
	load 0
	printi
	prints "\n"
	load 0 				/* n = n - 1 */
	const 1
	sub
	store 0
	jmp WHILE
END_WHILE:
	exit
	return

/* while 
int main()
{
	int n = 10;
	while (n > 0) {
		printf("n = ", n, "\n");
		n --;
	}
}
*/
