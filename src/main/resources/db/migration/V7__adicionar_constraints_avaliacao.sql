-- Reforça no banco duas regras que o AvaliacaoService já valida na
-- camada de serviço: um perfil não pode avaliar o mesmo filme mais de uma
-- vez, e a nota deve estar entre 1 e 5. A validação do Service dá a
-- mensagem de erro amigável; a constraint do banco é quem garante a regra
-- de fato sob concorrência (duas requisições simultâneas, por exemplo).
ALTER TABLE avaliacao
    ADD CONSTRAINT uk_avaliacao_perfil_filme UNIQUE (perfil_id, filme_id);

ALTER TABLE avaliacao
    ADD CONSTRAINT ck_avaliacao_nota CHECK (nota BETWEEN 1 AND 5);
