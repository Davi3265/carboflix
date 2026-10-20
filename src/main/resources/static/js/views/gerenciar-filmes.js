// Tela 07 Gerenciar filmes do protótipo (listagem + CRUD de /filmes).
import { api } from '../api.js';
import { $, $$, confirmar, esc, formatarMedia, icone, poster, textoClassificacao, toast } from '../ui.js';
import { abrirFormularioFilme } from './filme-form.js';

export async function render(raiz, ctx) {
  const filmes = await api.filmes();
  if (!ctx.ehAtual()) return;
  filmes.sort((a, b) => a.titulo.localeCompare(b.titulo, 'pt-BR'));

  raiz.innerHTML = `
    <div class="page">
      <div class="page-head">
        <div>
          <h1 class="h1">Gerenciar filmes</h1>
          <p>Cadastre, edite e remova os filmes do catálogo.</p>
        </div>
        <button class="btn btn-primary" data-novo>${icone.mais} Novo filme</button>
      </div>
      ${filmes.length ? `
        <div class="surface table-wrap" style="overflow:hidden">
          <table class="table">
            <thead><tr><th>Filme</th><th>Ano</th><th>Duração</th><th>Classif.</th><th>Categorias</th><th>Média</th><th>Ações</th></tr></thead>
            <tbody>
              ${filmes.map((f) => `
                <tr>
                  <td><a class="film-cell" href="#/filmes/${f.id}">${poster(f)}<span>${esc(f.titulo)}</span></a></td>
                  <td class="muted">${f.anoLancamento}</td>
                  <td class="muted">${f.duracaoMin} min</td>
                  <td><span class="badge-class" title="${esc(textoClassificacao(f.classificacaoIndicativa))}">${esc(f.classificacaoIndicativa)}</span></td>
                  <td><div class="chips">${f.categorias.map((c) => `<span class="chip">${esc(c.nome)}</span>`).join('') || '<span class="muted">—</span>'}</div></td>
                  <td>${f.totalAvaliacoes ? `<span class="rating">${icone.estrela}<strong>${formatarMedia(f.mediaAvaliacoes)}</strong></span>` : '<span class="muted">Sem notas</span>'}</td>
                  <td><div class="actions">
                    <button class="icon-btn" data-editar="${f.id}" title="Editar" aria-label="Editar ${esc(f.titulo)}">${icone.editar}</button>
                    <button class="icon-btn danger" data-excluir="${f.id}" title="Excluir" aria-label="Excluir ${esc(f.titulo)}">${icone.lixeira}</button>
                  </div></td>
                </tr>`).join('')}
            </tbody>
          </table>
        </div>`
      : '<div class="empty"><strong>Nenhum filme cadastrado.</strong>Use o botão "Novo filme" para começar o catálogo.</div>'}
    </div>`;

  $('[data-novo]', raiz).addEventListener('click', async () => {
    if (await abrirFormularioFilme(null)) ctx.recarregar();
  });
  $$('[data-editar]', raiz).forEach((b) => b.addEventListener('click', async () => {
    const filme = filmes.find((f) => f.id === Number(b.dataset.editar));
    if (await abrirFormularioFilme(filme)) ctx.recarregar();
  }));
  $$('[data-excluir]', raiz).forEach((b) => b.addEventListener('click', async () => {
    const filme = filmes.find((f) => f.id === Number(b.dataset.excluir));
    const ok = await confirmar({
      titulo: `Excluir "${filme.titulo}"?`,
      mensagem: 'O filme sai do catálogo. Filmes que já têm avaliações não podem ser excluídos.',
      confirmar: 'Excluir filme',
      acao: () => api.excluirFilme(filme.id),
    });
    if (ok) { toast('Filme excluído.'); ctx.recarregar(); }
  }));

  document.title = 'Gerenciar filmes · CarboFlix';
}
