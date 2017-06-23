import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Lucas vieira Alves on 20/06/17.
 */
public class MainApplication extends Application {

    private Stage primaryStage;
    private Integer mNumeroDeVariaveis;
    private Integer mNumeroDeClausulas;

    private ArrayList<ArrayList<Variavel>> mVariaveis = new ArrayList();
    private ArrayList<ArrayList<Variavel>> mEquacoes = new ArrayList();
    private BufferedReader br;
    private int quantidadeNegativos;
    private FileOutputStream fileOutputStream;

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.primaryStage = primaryStage;

        File arquivo = abrirChooserParaSelecionarArquivo();
        criarVariaveis(arquivo);

        printModeloDeArquivo(mVariaveis);

        criarArquivoParaExportarLingo(mVariaveis);


    }

    private void criarArquivoParaExportarLingo(ArrayList<ArrayList<Variavel>> mVariaveis) throws IOException {
        File arquivoLingo = criarArquivoVazio();

        fileOutputStream = new FileOutputStream(arquivoLingo);
        fileOutputStream.write("max ".getBytes());
        for(int z=0 ;z <mNumeroDeClausulas; z++){
            if(z % 40 ==0 && z!=0){
                fileOutputStream.write("\n".getBytes());
            }
            if(z==mNumeroDeClausulas-1){
                fileOutputStream.write(("z"+z).getBytes());

            }else {
                fileOutputStream.write(("z"+z+"+ ").getBytes());
            }

        }
        fileOutputStream.write(("\n").getBytes());
        fileOutputStream.write(("st ").getBytes());
        fileOutputStream.write("\n".getBytes());
        for(int i = 0; i< this.mVariaveis.size() ; i++){
            fileOutputStream.write(("z"+i+" ").getBytes());
            for(int j = 0; j< this.mVariaveis.get(i).size(); j++){
                fileOutputStream.write((this.mVariaveis.get(i).get(j).getValor()+"\t").getBytes());
            }
            fileOutputStream.write(("\n").getBytes());
        }
        int nMaisM =mNumeroDeVariaveis+mNumeroDeClausulas;
        fileOutputStream.write(("end").getBytes());
        fileOutputStream.write(("\n").getBytes());
        fileOutputStream.write(("int "+ nMaisM).getBytes());
    }

    private File criarArquivoVazio() throws IOException {
        File arquivoLingo = new File(System.getProperty("user.dir")+"/arquivolingo.txt");
        if(arquivoLingo.exists()){
            arquivoLingo.delete();
        }

        boolean foiCriado = arquivoLingo.createNewFile();

        if(foiCriado){
            System.out.println("Arquivo criado com sucesso");
        }else {
            System.out.println("Problema ao criar o arquivo");
        }
        return arquivoLingo;
    }


    private void printModeloDeArquivo(ArrayList<ArrayList<Variavel>> mVariaveis) throws IOException {
        System.out.print("max ");
        for(int z=0 ;z <mNumeroDeClausulas; z++){
            if(z % 40 ==0 && z!=0){
                System.out.println("");
            }
            if(z==mNumeroDeClausulas-1){
                System.out.print("z"+z);
            }else {
                System.out.print("z"+z+"+ ");
            }

        }
        System.out.println("");
        System.out.println("st ");
        for(int i = 0; i< this.mVariaveis.size() ; i++){
            System.out.print("z"+i+" ");
            for(int j = 0; j< this.mVariaveis.get(i).size(); j++){
                System.out.print(this.mVariaveis.get(i).get(j).getValor()+"\t");
            }
            System.out.println();
        }
        int nMaisM =mNumeroDeVariaveis+mNumeroDeClausulas;
        System.out.println("end");
        System.out.println("int "+ nMaisM);
    }

    private void criarVariaveis(File arquivo) throws IOException {
        br = new BufferedReader(new FileReader(arquivo));


        String primeiraLinha = br.readLine();
        lerPrimeiraLinha(primeiraLinha);


        String linha = primeiraLinha = br.readLine();
        int contador = 0;

        while (linha != null && !linha.equals("")) {
            contador++;


            String[] elementosDaLinha = linha.trim().split("\\s+");

            if(contador==666){
                System.out.println();
            }
            int numeroDeVariaveisParaEssaLinha = Integer.valueOf(elementosDaLinha[0]);
            int custoParaAquelaLinha = Integer.valueOf(elementosDaLinha[1]); // não iremos usar
            boolean devePegarVariaveisRemanecentes = false;

            if (numeroDeVariaveisParaEssaLinha > 4) {
                devePegarVariaveisRemanecentes = true;
            }
            ArrayList<Variavel> variavels = new ArrayList<>();

            variavels.addAll( pegarVariaveis(elementosDaLinha,numeroDeVariaveisParaEssaLinha, devePegarVariaveisRemanecentes)) ;

            mVariaveis.add(variavels);

            linha = br.readLine();
        }


    }

    private ArrayList<Variavel> pegarVariaveis(String[] elementosDaLinha, int numeroDeVariaveisParaEssaLinha, boolean devePegarVariaiesRemanecentes) {
        ArrayList<Variavel> variavels = new ArrayList<>();
        quantidadeNegativos =0 ;
        try {
            for (int i = 2; i <= numeroDeVariaveisParaEssaLinha+1; i++) {
                String variavel = elementosDaLinha[i];
                if(variavel.contains("-")){
                    quantidadeNegativos++;
                    variavels.add(new Variavel(("+x"+variavel.replace("-",""))));
                }else {
                    variavels.add(new Variavel("-x"+variavel));
                }

            }
        }catch (IndexOutOfBoundsException e){
            if (devePegarVariaiesRemanecentes) {
                variavels.addAll(pegarVariaveisRemanecentes(numeroDeVariaveisParaEssaLinha-4));
            }
        }

        variavels.add(new Variavel("<="+quantidadeNegativos));

        return variavels;

    }

    private ArrayList<Variavel> pegarVariaveisRemanecentes(int quantidadeDeElementosRemanecentes) {
        ArrayList<Variavel> variavelsRemanecentes = new ArrayList<>();
        try {
            String linha = br.readLine();
            String[] elementosDaLinha = linha.trim().split("\\s+");

            for (String variavel : elementosDaLinha) {
                if(variavel.contains("-")){
                    quantidadeNegativos++;
                    //variavelsRemanecentes.add(new Variavel("(1-"+"x"+variavel.replace("-","")+")"));)
                    variavelsRemanecentes.add(new Variavel(("+x"+variavel.replace("-",""))));
                }else {
                    variavelsRemanecentes.add(new Variavel("-x"+variavel));
                }

            }
            if(quantidadeDeElementosRemanecentes-elementosDaLinha.length>0){
                variavelsRemanecentes.addAll(pegarVariaveisRemanecentes(quantidadeDeElementosRemanecentes-elementosDaLinha.length));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return variavelsRemanecentes;
    }

    private void lerPrimeiraLinha(String primeiraLinha) {
        primeiraLinha = primeiraLinha.trim();
        mNumeroDeVariaveis = Integer.valueOf(primeiraLinha.substring(0, 3).trim()); // testar
        mNumeroDeClausulas = Integer.valueOf(primeiraLinha.substring(3).trim()); //testar
    }


    private File abrirChooserParaSelecionarArquivo() {
        primaryStage.setTitle("Escolha o arquivo");
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Escolha o arquivo");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Texto", "txt"));
        return fileChooser.showOpenDialog(primaryStage);

    }
}
