package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.dto.TemporadaDTO;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;


public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY;
    private List<SerieDTO> serieDTOS = new ArrayList<>();

    private SerieRepository repositorio;

    private List<Serie> series = new ArrayList<>();

    public Principal(SerieRepository repositorio, String apiKey) {
        this.repositorio = repositorio;
        this.API_KEY = apiKey;
    }

    public void exibeMenu() {
        var opcao = -1;
        while(opcao != 0) {
            var menu = """
                    
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                    4 - Buscar série por título
                    5 - Buscar séries por ator
                                    
                    0 - Sair       
                                             
                    """;

            System.out.println(menu);

            try {
                opcao = Integer.parseInt(leitura.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida! Digite apenas o número correspondente ao menu.\n");
                continue;
            }

            switch (opcao) {
                case 1 -> buscarSerieWeb();
                case 2 -> buscarEpisodioPorSerie();
                case 3 -> listarSeriesBuscadas();
                case 4 -> buscarSeriePorTitulo();
                case 5 -> buscarSeriesPorAtor();
                case 0 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida! Escolha um número disponível no menu.\n");
            }
        }
    }



    private SerieDTO getDadosSerie() {

        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        SerieDTO dados = conversor.obterDados(json, SerieDTO.class);
        return dados;
    }


    private void buscarSerieWeb() {

        SerieDTO dados = getDadosSerie();
        Serie serie = new Serie(dados);
        repositorio.save(serie);
        System.out.println(dados);
    }


    private void buscarEpisodioPorSerie(){

        listarSeriesBuscadas();
        System.out.println("\nDe qual série deseja buscar os episódios? ");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase()
                        .contains(nomeSerie.toLowerCase()))
                .findFirst();

        if (serie.isPresent()) {

            var serieEncontrada = serie.get();
            List<TemporadaDTO> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo()
                        .replace(" ", "+") + "&season=" + i + API_KEY);
                TemporadaDTO temporadaDTO = conversor.obterDados(json, TemporadaDTO.class);
                temporadas.add(temporadaDTO);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);

        } else {
            System.out.println("Série não encontrada!");
        }


    }


    private void listarSeriesBuscadas(){

        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }


    private void buscarSeriePorTitulo() {
        System.out.println("Escolha uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();
        Optional<Serie> serieBuscada = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBuscada.isPresent()) {
            System.out.println("Dados da série: " + serieBuscada.get());
        } else {
            System.out.println("Série não encontrada!");
        }
    }


    private void buscarSeriesPorAtor() {
        System.out.println("Insira um Ator para buscar suas séries: ");
        var nomeAtor = leitura.nextLine();
        List<Serie> seriesBuscadas = repositorio.findByAtoresContainingIgnoreCase(nomeAtor);

        if (seriesBuscadas.isEmpty()) {
            System.out.println("Nenhuma série encontrada com o ator: " + nomeAtor.toUpperCase());
        } else {
            System.out.println("\nSéries em que " + nomeAtor + " trabalhou:\n");
            seriesBuscadas.forEach(s -> System.out.printf(
                    """
                            Série: %s
                            Temporadas: %d
                            Avaliação: %.1f ⭐
                            -------------------------------%n""",
                    s.getTitulo(),
                    s.getTotalTemporadas(),
                    s.getAvaliacao()
        ));
        }
    }

}