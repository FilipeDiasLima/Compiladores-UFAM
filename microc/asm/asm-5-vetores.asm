_main_:
	enter 0 2
	const 2				/* a = new int[2]; */
	newarray
	store 0
	const 2				/* b = new int[2]; */
	newarray
	store 1
	load 0				/* a[0] = 10; */
	const 0
	const 10
	astore
	load 0				/* a[1] = 20; */
	const 1
	const 20
	astore
	load 1 				/* b[0] = a[1]; */
	const 0
	load 0
	const 1
	aload
	astore
	prints "b[0] = " 	/* printf("b[0] = ", b[0], "\n"); */
	load 1
	const 0
	aload
	printi
	prints "\n"
	exit 
	return

/* vetores 

void main()
{	
	int[] a, b;
	a = new int[2];
	b = new int[2];
	a[0] = 10;
	a[1] = 20;
	b[0] = a[1];
	printf("b[0] = ", b[0], "\n");
}

*/
