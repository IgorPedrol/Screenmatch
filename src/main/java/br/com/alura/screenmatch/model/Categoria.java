package br.com.alura.screenmatch.model;

public enum Categoria {
    ACAO("Action"),
    ROMANCE("Romance"),
    COMEDIA("Comedy"),
    DRAMA("Drama"),
    CRIME("Crime"),
    ANIMACAO("Animation"),
    ADULTO("Adult"),
    AVENTURA("Adventure"),
    BIOGRAFIA("Biography"),
    DOCUMENTARIO("Documentary"),
    FAMILIA("Family"),
    FANTASIA("Fantasy"),
    FILME_NOIR("Film-Noir"),
    PROGRAMA_DE_JOGOS("Game-Show"),
    HISTORIA("History"),
    TERROR("Horror"),
    MUSICA("Music"),
    MUSICAL("Musical"),
    MISTERIO("Mystery"),
    NOTICIAS("News"),
    REALITY_TV("Reality-TV"),
    FICCAO_CIENTIFICA("Sci-Fi"),
    CURTA("Short"),
    ESPORTE("Sport"),
    TALK_SHOW("Talk-Show"),
    SUSPENSE("Thriller"),
    GUERRA("War"),
    FAROESTE("Western");

    private String categoriaOmdb;

    Categoria(String categoriaOmdb){
        this.categoriaOmdb = categoriaOmdb;
    }

    public static Categoria fromString(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaOmdb.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }
}
