void imprime(int v[])
{
	int i;
	for (i = 0; i < len(v); i = i + 1)
		printf(v[i], " ");
	/* printf("\n"); */
}
void copia (int v1[], int v2[])
{
	int i;
	for (i = 0; i < len(v1); i = i + 1)
		v1[i] = v2[i];
}
void troque(int v[], int i, int j)
{
	int tmp;
	tmp = v[i];
	v[i] = v[j];
	v[j] = tmp;
}
/**** quicksort ****/
int custoq;
int globalvar;
int iMenorFimEviMenorPivo(int i, int fim, int v[], int pivo)
{
	if (i < fim) if (v[i] < pivo) return 1;
	return 0;
}
/* particione */
int particione(int v[], int ini, int fim) 
{
	int i, j, pivo;
	i = ini + 1;
	j = fim;
	pivo = v[ini];
	while (i <= j) {
		while (iMenorFimEviMenorPivo(i, fim, v, pivo) == 1) {
			i = i + 1;
			custoq = custoq + 1;
		}
		while (v[j] > pivo) {
			j = j - 1;
			custoq = custoq + 1;
		}
		custoq = custoq + 2;
		if (i < j) {
			troque(v, i, j);
			i = i + 1;
			j = j - 1;
		} 
		else
			i = i + 1;
		}
		troque(v, j, ini);
		return j;
	}
	/* quicksort */
int quicksort(int v[], int ini, int fim) {
	custoq = 0;
	if ((fim - ini) < 1)
	return 0;
	int p;
	p = particione(v, ini, fim);
	if (ini < (p - 1))
		quicksort(v, ini, p - 1);
	if (fim > (p + 1))
		quicksort(v, p + 1, fim);
	return custoq;
}
/***** main *******/
void main()
{
	int custo, i;
	int v[], v2[];
	v = new int { 61, 57, 72, 18,
	8, 30, 21, 93 };
	v2 = new int [len(v)];
	printf(" ORIGINAL = ");
	imprime(v); printf("\n");
	copia(v2, v);
	custo = quicksort(v2, 0, 7);
	printf("QUICKSORT = ");
	imprime(v2);
	printf("custo = ", custo, "\n");
}