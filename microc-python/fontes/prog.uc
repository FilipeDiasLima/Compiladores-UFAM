/* vetores */
void imprime(int v[]) {
	int i; i = 0;
	printf("[ ");
	while (i < len(v)) {
		printf(v[i], " ");
		i = i + 1;
	}
	printf("]\n");
}

void soma(int v1[], int v2[], int v3[]) {
	int i; i = 0;
	while (i < len(v1)) {
		v3[i] = v1[i] + v2[i];
		i = i + 1;
	}
}

void diferenca(int v1[], int v2[], int v3[]) {
	int i; i = 0;
	while (i < len(v1)) {
		v3[i] = v1[i] - v2[i];
		i = i + 1;
	}
}

void produto(int v1[], int v2[], int v3[]) {
	int i; i = 0;
	while (i < len(v1)) {
		v3[i] = v1[i] * v2[i];
		i = i + 1;
	}
}

void main() {
	int v1[], v2[], v3[];
	int i, n;
	printf("Dimensao dos vetores? "); scanf(n);
	v1 = new int[n];
	v2 = new int[n];
	v3 = new int[n];
	printf("\nEntre com v1:\n");
	i = 0;
	while (i < n) {
		printf("- v1[", i, "]? "); scanf(v1[i]);
		i = i + 1;
	}
	printf("\nEntre com v2:\n");
	i = 0;
	while (i < n) {
		printf("- v2[", i, "]? "); scanf(v2[i]);
		i = i + 1;
	}

	soma(v1, v2, v3);
	printf("\nSoma: "); imprime(v3);
	diferenca(v1, v2, v3);
	printf("Diferenca: "); imprime(v3);
	produto(v1, v2, v3);
	printf("Produto: "); imprime(v3);
}