/* assembly */
_main_: 
	enter 0 3
	scani				/* scanf(a) */
	store 0
	scani				/* scanf(b) */
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
	int a, b, c;
	scanf(a);
	scanf(b);
	c = a + b;
	printf("a+b = ", c, "\n");
}
*/
