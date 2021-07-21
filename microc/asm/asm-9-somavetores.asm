
/* Soma de vetores 

void loadv(int[] v)
{	
	int i; i = 0;
	while (i < strlen(v)) {
		printf("[", i , "] = "); scanf(v[i]);
		i ++;
	}
}

void printv(int[] v)
{
	int i; i = 0;
	while (i < strlen(v)) {
		printf(v[i], " "); 
		i ++;
	}
}

void somav(int[] v1, int[] v2, int[] v3)
{
	int i; i = 0;
	while (i < strlen(v1)) {
		v3[i] = v1[i] + v2[i]; 
		i ++;
	}
}

int main()
{
	int[] v1, v2, v3;
	int n, i;
	printf("Qts elementos? "); scanf(n);
	if (n<1) 
		return;
	v1 = new int[n];
	v2 = new int[n];
	v3 = new int[n];
	loadv(v1);
	loadv(v2);
	somav(v1, v2, v3);
	printf("v3 = [", printv(v3), "]\n");
}

