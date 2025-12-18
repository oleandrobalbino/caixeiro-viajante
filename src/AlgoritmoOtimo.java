import java.util.ArrayDeque;

public class AlgoritmoOtimo {
    private final double[][] matriz;
    private final int n;
    private double melhorCusto = Double.MAX_VALUE;
    private int[] melhorCaminho;

    class Resultado {
        private double custoMinimo;
        private int[] rota;
    
        public Resultado(double custoMinimo, int[] rota) {
            this.custoMinimo = custoMinimo;
            this.rota = rota;
        }
    
        public double getCustoMinimo() {
            return custoMinimo;
        }
    
        public int[] getRota() {
            return rota;
        }
    }

    class No {
        int nivel;
        int vertice;
        double custo;
        double limiteInferior;
        int[] caminho;
        boolean[] visitado;
    
        public No(int nivel, int vertice, double custo, double limiteInferior, int[] caminho, boolean[] visitado) {
            this.nivel = nivel;
            this.vertice = vertice;
            this.custo = custo;
            this.limiteInferior = limiteInferior;
            this.caminho = caminho.clone();
            this.visitado = visitado.clone();
        }
    }

    public AlgoritmoOtimo(double[][] matriz) {
        this.matriz = matriz;
        this.n = matriz.length;
        this.melhorCaminho = new int[n + 1];
    }

    private double calcularLimiteInferior(int vertice, boolean[] visitado) {
        double limite = 0;
        for (int i = 0; i < n; i++) {
            if (!visitado[i]) {
                double menor = Double.MAX_VALUE;
                for (int j = 0; j < n; j++) {
                    if (i != j && matriz[i][j] < menor) {
                        menor = matriz[i][j];
                    }
                }
                limite += menor;
            }
        }
        return limite;
    }

    public Resultado resolver() {
        ArrayDeque<No> pilha = new ArrayDeque<>();
        boolean[] visitado = new boolean[n];
        visitado[0] = true;
        int[] caminho = new int[n + 1];
        caminho[0] = 0;
        double limiteInicial = calcularLimiteInferior(0, visitado);
        pilha.push(new No(0, 0, 0, limiteInicial, caminho, visitado));

        while (!pilha.isEmpty()) {
            No no = pilha.pop();
            if (no.limiteInferior >= melhorCusto) continue;

            if (no.nivel == n - 1) {
                double custoFinal = no.custo + matriz[no.vertice][0];
                if (custoFinal < melhorCusto) {
                    melhorCusto = custoFinal;
                    System.arraycopy(no.caminho, 0, melhorCaminho, 0, n);
                    melhorCaminho[n] = 0;
                }
                continue;
            }

            for (int prox = n - 1; prox >= 0; prox--) {
                if (!no.visitado[prox]) {
                    no.visitado[prox] = true;
                    no.caminho[no.nivel + 1] = prox;
                    double novoCusto = no.custo + matriz[no.vertice][prox];
                    if (novoCusto >= melhorCusto) {
                        no.visitado[prox] = false;
                        continue;
                    }
                    double novoLimite = novoCusto + calcularLimiteInferior(prox, no.visitado);
                    if (novoLimite < melhorCusto) {
                        pilha.push(new No(no.nivel + 1, prox, novoCusto, novoLimite, no.caminho, no.visitado));
                    }
                    no.visitado[prox] = false;
                }
            }
        }
        return new Resultado(melhorCusto, melhorCaminho);
    }

}