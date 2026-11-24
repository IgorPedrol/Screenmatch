package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.dto.TemporadaDTO;
import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;


public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY;
    private List<SerieDTO> serieDTOS = new ArrayList<>();

    private List<Serie> series = new ArrayList<>();

    private Optional<Serie> serieBusca;

    private SerieRepository repositorio;



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
                     6 - Listar as Top 5 Séries
                     7 - Buscar Séries por categoria
                     8 - Buscar Séries com poucas temporadas
                     9 - Buscar Episódios por trecho
                    10 - Listar Melhores Episódios de uma Série
                    11 - Buscar Episódios a partir de uma Data
                                    
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
                case 6 -> listarTop5Series();
                case 7 -> buscarSeriesPorCategoria();
                case 8 -> buscarSeriesPoucasTemporadasBemAvaliadas();
                case 9 -> buscarEpisodioPorTrecho();
                case 10 -> topEpisodiosPorSerie();
                case 11 -> buscarEpisodiosAPartirDe();
                case 0 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida! Escolha um número disponível no menu.\n");
            }
        }
    }




    private SerieDTO getDadosSerie() {

        System.out.println("Digite o nome da série para busca: ");
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

        System.out.println("-------------------------------");
        series.forEach(s -> System.out.printf(
                """
                        Série: %s
                        Temporadas: %d
                        Avaliação: %.1f ⭐
                        Gênero: %s
                        -------------------------------%n""",
                s.getTitulo(),
                s.getTotalTemporadas(),
                s.getAvaliacao(),
                s.getGenero()
        ));
    }


    private void buscarSeriePorTitulo() {
        System.out.println("Escolha uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();
        serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBusca.isPresent()) {
            System.out.printf(
                    """
                            -------------------------------
                            Série: %s
                            Temporadas: %d
                            Avaliação: %.1f ⭐
                            Gênero: %s
                            Atores principais: %s
                            Sinopse: %s
                            -------------------------------%n""",
                    serieBusca.get().getTitulo(),
                    serieBusca.get().getTotalTemporadas(),
                    serieBusca.get().getAvaliacao(),
                    serieBusca.get().getGenero(),
                    serieBusca.get().getAtores(),
                    serieBusca.get().getSinopse()
            );
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


    private void listarTop5Series() {

        List<Serie> seriesTop = repositorio.findTop5ByOrderByAvaliacaoDesc();

        System.out.println("\nTop 5 Séries mais bem avaliadas: \n");
        seriesTop.forEach(s -> System.out.printf(
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


    private void buscarSeriesPorCategoria() {

        System.out.println("Deseja buscar séries de qual categoria/gênero? ");
        var nomeGenero = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeGenero);

        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);

        System.out.println("\nSéries do Gênero: " + nomeGenero + "\n");
        seriesPorCategoria.forEach(s -> System.out.printf(
                """
                        Série: %s
                        Gênero: %s
                        Temporadas: %d
                        Avaliação: %.1f ⭐
                        -------------------------------%n""",
                s.getTitulo(),
                s.getGenero(),
                s.getTotalTemporadas(),
                s.getAvaliacao()
        ));

    }


    private void buscarSeriesPoucasTemporadasBemAvaliadas() {

        System.out.println("\nQuer buscar Séries com até quantas temporadas? ");
        var temporadasMax = leitura.nextInt();
        leitura.nextLine();

        System.out.println("\nDeseja ver Séries com avaliação maior que: ");
        var avalicaoMin = leitura.nextDouble();
        leitura.nextLine();

        List<Serie> seriesCurtas = repositorio.seriesPorTemporadaEAvaliacao(temporadasMax, avalicaoMin);

        System.out.println("\nAs Melhores Séries com até " + temporadasMax + " temporadas: \n");
        seriesCurtas.forEach(s -> System.out.printf(
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


    private void buscarEpisodioPorTrecho() {

        System.out.println("Qual o nome do episódio para busca? ");
        var trechoEpisodio = leitura.nextLine();


        List<Episodio> episodiosEncontrados = repositorio.episodiosPorTrecho(trechoEpisodio);
        System.out.println("-------------------------------");
        episodiosEncontrados.forEach(e -> System.out.printf(
                """
                        Série: %s
                        Temporada: %d
                        Episódio: %s - %s
                        -------------------------------%n""",
                e.getSerie().getTitulo(),
                e.getTemporada(),
                e.getNumeroEpisodio(),
                e.getTitulo()
        ));
    }


    private void topEpisodiosPorSerie() {

        buscarSeriePorTitulo();

        System.out.println("Qual o tamanho do Top? (Top 5, 10) ");
        var top = leitura.nextInt();
        leitura.nextLine();

        if (serieBusca.isPresent()) {
            Serie serie = serieBusca.get();
            List<Episodio> topEpisodios = repositorio.topEpisodiosPorSerie(serie, top);

            System.out.printf("%n=== Top %d episódios de %s ===%n", top, serie.getTitulo());
            for (int i = 0; i < top; i++) {
                Episodio e = topEpisodios.get(i);
                System.out.printf(
                        """
                                #%d
                                Temporada: %d
                                Episódio: %s - %s
                                Avaliação: %.1f ⭐
                                -------------------------------%n""",
                        i + 1,
                        e.getTemporada(),
                        e.getNumeroEpisodio(),
                        e.getTitulo(),
                        e.getAvaliacao()
                );
            }
        }


    }


    private void buscarEpisodiosAPartirDe() {

        buscarSeriePorTitulo();

        if (serieBusca.isPresent()) {
            System.out.println("Listar episódios a partir de: ");
            var anoLancamento = leitura.nextInt();
            leitura.nextLine();

            List<Episodio> episodiosAno = repositorio.episodiosPorSerieEAno(serieBusca.get(), anoLancamento);

            System.out.printf("%n=== Episódios de %s a partir de: %d  ===%n", serieBusca.get().getTitulo(), anoLancamento);
            episodiosAno.forEach(e -> System.out.printf(
                    """
                            Temporada: %d
                            Episódio: %s - %s
                            Lançado em: %s
                            Avaliação: %.1f ⭐
                            -------------------------------%n""",
                    e.getTemporada(),
                    e.getNumeroEpisodio(),
                    e.getTitulo(),
                    e.getDataLancamento(),
                    e.getAvaliacao()
            ));
        }

    }


}