package br.com.alura.screenmatch.model;

public enum Categoria {
    ACAO("Action", "Ação"),
    ROMANCE("Romance", "Romance"),
    COMEDIA("Comedy", "Comédia"),
    DRAMA("Drama", "Drama"),
    CRIME("Crime", "Crime"),
    ANIMACAO("Animation", "Animação"),
    ADULTO("Adult", "Adulto"),
    AVENTURA("Adventure", "Aventura"),
    BIOGRAFIA("Biography", "Biografia"),
    DOCUMENTARIO("Documentary", "Documentário"),
    FAMILIA("Family", "Família"),
    FANTASIA("Fantasy", "Fantasia"),
    FILME_NOIR("Film-Noir", "Filme-Noir"),
    PROGRAMA_DE_JOGOS("Game-Show", "Programa-de-jogos"),
    HISTORIA("History", "História"),
    TERROR("Horror", "Terror"),
    MUSICA("Music", "Música"),
    MUSICAL("Musical", "Musical"),
    MISTERIO("Mystery", "Mistério"),
    NOTICIAS("News", "Notícias"),
    REALITY_TV("Reality-TV", "Reality-TV"),
    FICCAO_CIENTIFICA("Sci-Fi", "Ficção-Científica"),
    CURTA("Short", "Curta"),
    ESPORTE("Sport", "Esporte"),
    TALK_SHOW("Talk-Show", "Talk-Show"),
    SUSPENSE("Thriller", "Suspense"),
    GUERRA("War", "Guerra"),
    FAROESTE("Western", "Faroeste");

    private String categoriaOmdb;

    private String categoriaPortugues;

    Categoria(String categoriaOmdb, String categoriaPortugues) {

        this.categoriaOmdb = categoriaOmdb;
        this.categoriaPortugues = categoriaPortugues;
    }

    public static Categoria fromString(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaOmdb.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }

    public static Categoria fromPortugues(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaPortugues.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }
}
