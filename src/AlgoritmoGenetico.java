import java.util.*;

public class AlgoritmoGenetico {
    private int[][] grafo;
    private int numCidades;
    private Random aleatorio = new Random();
    private int tipoMutacao = 3;
    private int[] melhorRotaGlobal;
    private int custoMelhorRotaGlobal = Integer.MAX_VALUE;
    private ProgressaoEmTempoReal progressListener;
    private boolean habilitar2Opt = false;

    public void setHabilitar2Opt(boolean habilitar2Opt) {
        this.habilitar2Opt = habilitar2Opt;
    }

    public void setProgressListener(ProgressaoEmTempoReal listener) {
        this.progressListener = listener;
    }

    public AlgoritmoGenetico(int[][] grafo) {
        this.grafo = grafo;
        this.numCidades = grafo.length;
    }

    public void setTipoMutacao(int tipoMutacao) {
        this.tipoMutacao = tipoMutacao;
    }

    public int[] resolver(int tamanhoPopulacao, int numeroGeracoes, double taxaMutacao, int numeroPais) {
        int[][] populacao = gerarPopulacaoInicial(tamanhoPopulacao);
        melhorRotaGlobal = null;
        custoMelhorRotaGlobal = Integer.MAX_VALUE;

        for (int geracao = 0; geracao < numeroGeracoes; geracao++) {
            populacao = evoluir(populacao, numeroPais, taxaMutacao);

            int[] melhorRotaAtual = obterMelhorRota(populacao);
            int custoMelhorRotaAtual = calcularCustoRota(melhorRotaAtual);
            if (custoMelhorRotaAtual < custoMelhorRotaGlobal) {
                melhorRotaGlobal = Arrays.copyOf(melhorRotaAtual, melhorRotaAtual.length);
                custoMelhorRotaGlobal = custoMelhorRotaAtual;
            }

            double custoMedio = calcularCustoMedio(populacao);

            if (progressListener != null) {
                progressListener.onGenerationComplete(geracao + 1, custoMedio, custoMelhorRotaGlobal);
            }
        }

        return melhorRotaGlobal;
    }

    private double calcularCustoMedio(int[][] populacao) {
        int somaCustos = 0;
        for (int[] rota : populacao) {
            somaCustos += calcularCustoRota(rota);
        }
        return (double) somaCustos / populacao.length;
    }

    private int[][] gerarPopulacaoInicial(int tamanhoPopulacao) {
        int[][] populacao = new int[tamanhoPopulacao][numCidades + 1];
        for (int i = 0; i < tamanhoPopulacao; i++) {
            populacao[i] = gerarRotaAleatoria();
        }
        return populacao;
    }

    private int[] gerarRotaAleatoria() {
        int[] rota = new int[numCidades + 1];
        for (int i = 0; i < numCidades; i++) {
            rota[i] = i;
        }
        for (int i = numCidades - 1; i > 0; i--) {
            int j = aleatorio.nextInt(i + 1);
            int temp = rota[i];
            rota[i] = rota[j];
            rota[j] = temp;
        }
        rota[numCidades] = rota[0];
        return rota;
    }

    private int[] aplicar2Opt(int[] rota) {
        int[] novaRota = Arrays.copyOf(rota, rota.length);
        int melhorCusto = calcularCustoRota(novaRota);
        boolean melhorou;
    
        do {
            melhorou = false;
            for (int i = 1; i < numCidades - 1; i++) {
                for (int j = i + 1; j < numCidades; j++) {
                    int[] rotaCandidata = aplicarTroca2Opt(novaRota, i, j);
                    int custoCandidato = calcularCustoRota(rotaCandidata);
                    if (custoCandidato < melhorCusto) {
                        novaRota = rotaCandidata;
                        melhorCusto = custoCandidato;
                        melhorou = true;
                    }
                }
            }
        } while (melhorou);
    
        return novaRota;
    }
    
    private int[] aplicarTroca2Opt(int[] rota, int i, int j) {
        int[] novaRota = Arrays.copyOf(rota, rota.length);
        while (i < j) {
            int temp = novaRota[i];
            novaRota[i] = novaRota[j];
            novaRota[j] = temp;
            i++;
            j--;
        }
        return novaRota;
    }

    private int[][] evoluir(int[][] populacao, int numeroPais, double taxaMutacao) {
        int[][] novaPopulacao = new int[populacao.length][numCidades + 1];
    
        int[] melhorRotaAtual = obterMelhorRota(populacao);
        novaPopulacao[0] = melhorRotaAtual;
    
        for (int i = 1; i < populacao.length; i++) {
            int[] pai1 = selecionarPorRoleta(populacao);
            int[] pai2 = selecionarPorRoleta(populacao);
            int[] filho = cruzamento(pai1, pai2);
    
            if (aleatorio.nextDouble() < taxaMutacao) {
                aplicarMutacao(filho, taxaMutacao);
            }
    
            if (habilitar2Opt) {
                filho = aplicar2Opt(filho);
            }
    
            novaPopulacao[i] = filho;
        }
    
        return novaPopulacao;
    }

    private int[] selecionarPorRoleta(int[][] populacao) {
        int custoMinimo = encontrarCustoMinimo(populacao);
    
        double[] aptidoes = new double[populacao.length];
        double aptidaoTotal = 0;
    
        for (int i = 0; i < populacao.length; i++) {
            int custo = calcularCustoRota(populacao[i]);
            aptidoes[i] = 1.0 / (custo - custoMinimo + 1);
            aptidaoTotal += aptidoes[i];
        }
    
        double[] probabilidades = new double[populacao.length];
        for (int i = 0; i < populacao.length; i++) {
            probabilidades[i] = aptidoes[i] / aptidaoTotal;
        }
    
        double valorRoleta = aleatorio.nextDouble();
        double somaProbabilidades = 0;
    
        for (int i = 0; i < populacao.length; i++) {
            somaProbabilidades += probabilidades[i];
            if (valorRoleta <= somaProbabilidades) {
                return populacao[i];
            }
        }
    
        return populacao[populacao.length - 1];
    }
    
    private int encontrarCustoMinimo(int[][] populacao) {
        int custoMinimo = Integer.MAX_VALUE;
        for (int[] rota : populacao) {
            int custo = calcularCustoRota(rota);
            if (custo < custoMinimo) {
                custoMinimo = custo;
            }
        }
        return custoMinimo;
    }

    private int[] cruzamento(int[] pai1, int[] pai2) {
        Map<Integer, Set<Integer>> tabelaArestas = new HashMap<>();
        for (int i = 0; i < numCidades; i++) {
            tabelaArestas.put(i, new HashSet<>());
        }
    
        preencherTabelaArestas(pai1, tabelaArestas);
        preencherTabelaArestas(pai2, tabelaArestas);
    
        int cidadeAtual = aleatorio.nextInt(numCidades);

        int[] filho = new int[numCidades + 1];
        boolean[] visitadas = new boolean[numCidades];
        filho[0] = cidadeAtual;
        visitadas[cidadeAtual] = true;
    
        for (int i = 1; i < numCidades; i++) {
            removerCidadeDaTabela(tabelaArestas, cidadeAtual);

            cidadeAtual = escolherProximaCidade(tabelaArestas, cidadeAtual, visitadas);
    
            filho[i] = cidadeAtual;
            visitadas[cidadeAtual] = true;
        }
    
        filho[numCidades] = filho[0];
    
        return filho;
    }
    
    private void preencherTabelaArestas(int[] rota, Map<Integer, Set<Integer>> tabelaArestas) {
        for (int i = 0; i < numCidades; i++) {
            int cidadeAtual = rota[i];
            int cidadeAnterior = rota[(i - 1 + numCidades) % numCidades];
            int cidadeProxima = rota[(i + 1) % numCidades];
    
            tabelaArestas.get(cidadeAtual).add(cidadeAnterior);
            tabelaArestas.get(cidadeAtual).add(cidadeProxima);
        }
    }
    
    private void removerCidadeDaTabela(Map<Integer, Set<Integer>> tabelaArestas, int cidade) {
        for (Set<Integer> arestas : tabelaArestas.values()) {
            arestas.remove(cidade);
        }
    }
    
    private int escolherProximaCidade(Map<Integer, Set<Integer>> tabelaArestas, int cidadeAtual, boolean[] visitadas) {
        Set<Integer> arestasDisponiveis = tabelaArestas.get(cidadeAtual);
        if (arestasDisponiveis.isEmpty()) {
            for (int i = 0; i < numCidades; i++) {
                if (!visitadas[i]) {
                    return i;
                }
            }
        }
    
        int proximaCidade = -1;
        int menorNumeroArestas = Integer.MAX_VALUE;
    
        for (int cidade : arestasDisponiveis) {
            if (!visitadas[cidade] && tabelaArestas.get(cidade).size() < menorNumeroArestas) {
                proximaCidade = cidade;
                menorNumeroArestas = tabelaArestas.get(cidade).size();
            }
        }
    
        if (proximaCidade == -1) {
            for (int i = 0; i < numCidades; i++) {
                if (!visitadas[i]) {
                    return i;
                }
            }
        }
    
        return proximaCidade;
    }

    private void aplicarMutacao(int[] rota, double taxaMutacao) {
        if (aleatorio.nextDouble() < taxaMutacao) {
            switch (tipoMutacao) {
                case 1:
                    mutacaoInsercao(rota);
                    break;
                case 2:
                    mutacaoMistura(rota);
                    break;
                case 3:
                    mutacaoTroca(rota);
                    break;
                case 4:
                    mutacaoInversao(rota);
                    break;
                default:
                    mutacaoTroca(rota);
            }
        }
    }

    private void mutacaoInsercao(int[] rota) {
        int i = aleatorio.nextInt(numCidades - 1) + 1;
        int j = aleatorio.nextInt(numCidades - 1) + 1;
        while (i == j) {
            j = aleatorio.nextInt(numCidades - 1) + 1;
        }

        int cidade = rota[i];
        if (i < j) {
            for (int k = i; k < j; k++) {
                rota[k] = rota[k + 1];
            }
        } else {
            for (int k = i; k > j; k--) {
                rota[k] = rota[k - 1];
            }
        }
        rota[j] = cidade;
    }

    private void mutacaoMistura(int[] rota) {
        int ponto1 = aleatorio.nextInt(numCidades - 1) + 1;
        int ponto2 = ponto1 + aleatorio.nextInt(numCidades - ponto1);
        if (ponto1 > ponto2) {
            int temp = ponto1;
            ponto1 = ponto2;
            ponto2 = temp;
        }

        for (int i = ponto1; i <= ponto2; i++) {
            int j = aleatorio.nextInt(ponto2 - ponto1 + 1) + ponto1;
            int temp = rota[i];
            rota[i] = rota[j];
            rota[j] = temp;
        }
    }

    private void mutacaoTroca(int[] rota) {
        int i = aleatorio.nextInt(numCidades - 1) + 1;
        int j = aleatorio.nextInt(numCidades - 1) + 1;
        while (i == j) {
            j = aleatorio.nextInt(numCidades - 1) + 1;
        }

        int temp = rota[i];
        rota[i] = rota[j];
        rota[j] = temp;
    }

    private void mutacaoInversao(int[] rota) {
        int ponto1 = aleatorio.nextInt(numCidades - 1) + 1;
        int ponto2 = ponto1 + aleatorio.nextInt(numCidades - ponto1);
        if (ponto1 > ponto2) {
            int temp = ponto1;
            ponto1 = ponto2;
            ponto2 = temp;
        }

        while (ponto1 < ponto2) {
            int temp = rota[ponto1];
            rota[ponto1] = rota[ponto2];
            rota[ponto2] = temp;
            ponto1++;
            ponto2--;
        }
    }

    public int calcularCustoRota(int[] rota) {
        if (!rotaValida(rota)) {
            throw new IllegalArgumentException("Rota inválida: a rota não visita todas as cidades exatamente uma vez.");
        }

        int custo = 0;
        for (int i = 0; i < numCidades; i++) {
            custo += grafo[rota[i]][rota[(i + 1) % numCidades]];
        }
        return custo;
    }

    private boolean rotaValida(int[] rota) {
        if (rota.length != numCidades + 1) {
            return false;
        }

        if (rota[0] != rota[numCidades]) {
            return false;
        }

        boolean[] visitadas = new boolean[numCidades];
        for (int i = 0; i < numCidades; i++) {
            int cidade = rota[i];
            if (cidade < 0 || cidade >= numCidades || visitadas[cidade]) {
                return false;
            }
            visitadas[cidade] = true;
        }

        return true;
    }

    private int[] obterMelhorRota(int[][] populacao) {
        int[] melhorRota = populacao[0];
        int menorCusto = calcularCustoRota(melhorRota);
        for (int i = 1; i < populacao.length; i++) {
            int custo = calcularCustoRota(populacao[i]);
            if (custo < menorCusto) {
                melhorRota = populacao[i];
                menorCusto = custo;
            }
        }
        return melhorRota;
    }

    public static int[][] lerGrafoDeArquivo(String caminho) {
        FileManager gerenciadorArquivo = new FileManager();
        List<String> linhas = gerenciadorArquivo.stringReader(caminho);
        if (linhas == null || linhas.isEmpty()) return null;

        int numCidades = Integer.parseInt(linhas.get(0));
        int[][] grafo = new int[numCidades][numCidades];

        for (int i = 1; i < linhas.size(); i++) {
            String[] partes = linhas.get(i).split(" ");
            int origem = Integer.parseInt(partes[0]);
            for (int j = 1; j < partes.length; j++) {
                String[] aresta = partes[j].replace(";", "").split("-");
                int destino = Integer.parseInt(aresta[0]);
                int peso = Integer.parseInt(aresta[1]);
                grafo[origem][destino] = peso;
                grafo[destino][origem] = peso;
            }
        }
        return grafo;
    }
}