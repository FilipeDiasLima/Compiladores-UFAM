/* assembly */
_main_: 
	enter 0 1
	const 3				/* a = 3 */
	putstatic 0
	const 7				/* b = 7 */
	putstatic 1
	getstatic 0  			/* c = a + b */
	getstatic 1
	add
	store 0
	prints "a+b = " 	/* printf("a+b = ", c, "\n") */
	load 0
	printi
	prints "\n"
	exit
	return

/*

int a, b;

int main()
{
	int c;
	a = 3;
	b = 7;
	c = a + b;
	printf("a+b = ", c, "\n");
}

*/
