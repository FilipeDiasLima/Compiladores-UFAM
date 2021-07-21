
_main_:
	enter 0 2
	prints "a? "		/* printf("a? "); scanf(a); */
	scani
	store 0
	prints "b? "		/* printf("b? "); scanf(b); */
	scani
	store 1
	load 0
	load 1
	jle ELSE 
	prints "a > b\n"
	jmp END
ELSE:
	prints "a <= b\n"
END:
	exit
	return

/* saltos 
void main()
{
	int a, b;
	printf("a? "); scanf(a);
	printf("b? "); scanf(b);
	if (a>b) printf("a > b\n");
	else printf("a <= b\n");
}
*/