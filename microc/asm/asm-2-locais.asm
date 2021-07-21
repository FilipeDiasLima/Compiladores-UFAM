/* assembly */
_main_: 
	enter 0 3
	const 3				/* a = 3 */
	store 0
	const 7				/* b = 7 */
	store 1
	load 0  			/* c = a + b */
	load 1
	add
	store 2
	prints "a+b = " 	/* printf("a+b = ", c, "\n") */
	load 2
	printi
	prints "\n"
	exit
	return

/*

int main()
{
	int a, b, c; /* 0, 1, 2 */
	a = 3;
	b = 7;
	c = a + b;
	printf("a+b = ", c, "\n");
}

*/