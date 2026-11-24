package com.pucgo.edu.treinamais.dto.response;

import java.util.List;

public class CatalogoExercicioPageResponse {

    private List<CatalogoExercicioResponse> itens;
    private long total;
    private boolean hasNextPage;
    private String nextCursor;
    private String origem;

    public CatalogoExercicioPageResponse() {
    }

    public CatalogoExercicioPageResponse(
            List<CatalogoExercicioResponse> itens,
            long total,
            boolean hasNextPage,
            String nextCursor,
            String origem) {
        this.itens = itens;
        this.total = total;
        this.hasNextPage = hasNextPage;
        this.nextCursor = nextCursor;
        this.origem = origem;
    }

    public List<CatalogoExercicioResponse> getItens() {
        return itens;
    }

    public void setItens(List<CatalogoExercicioResponse> itens) {
        this.itens = itens;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public String getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(String nextCursor) {
        this.nextCursor = nextCursor;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }
}
