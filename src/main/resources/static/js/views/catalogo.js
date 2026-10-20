// Tela 05 Catálogo do protótipo (GET /filmes e /categorias).
import { api } from '../api.js';
import { $, $$, esc, estrelas, formatarMedia, icone, metaFilme, plural, poster } from '../ui.js';

export async function render(raiz, ctx) {
  const [filmes, categorias] = await Promise.all([api.filmes(), api.categorias()]);
  if (!ctx.ehAtual()) return;
  categorias.sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR'));
  let categoriaId = null;
  let busca = '';

  const avaliados = filmes.filter((f) => f.totalAvaliacoes > 0)
    .sort((a, b) => b.mediaAvaliacoes - a.mediaAvaliacoes || b.totalAvaliacoes - a.totalAvaliacoes);
  const destaque = avaliados[0] || [...filmes].sort((a, b) => b.id - a.id)[0];

  if (!filmes.length) {
    raiz.innerHTML = `
      <div class="section">
        <div class="empty">
          <strong>O catálogo ainda está vazio.</strong>
          Cadastre categorias e filmes para começar.
          <div class="row" style="justify-content:center;margin-top:18px">
            <a class="btn btn-secondary" href="#/categorias">Criar categorias</a>
            <a class="btn btn-primary" href="#/gerenciar">${icone.mais} Cadastrar filme</a>
          </div>
        </div>
      </div>`;
    return;
  }

  raiz.innerHTML = `
    <section class="hero">
      <div class="hero-text">
        <span class="seal">${icone.estrela} ${avaliados.length ? 'Mais bem avaliado' : 'Novidade no catálogo'}</span>
        <h1>${esc(destaque.titulo)}</h1>
        <div class="meta-line">
          ${destaque.totalAvaliacoes ? `${estrelas(destaque.mediaAvaliacoes)}<strong>${formatarMedia(destaque.mediaAvaliacoes)} · ${plural(destaque.totalAvaliacoes, 'avaliação', 'avaliações')}</strong>` : '<span class="muted">Ainda sem avaliações</span>'}
          <span class="muted">${metaFilme(destaque)}</span>
        </div>
        ${destaque.sinopse ? `<p>${esc(destaque.sinopse)}</p>` : ''}
        <div class="row">
          <a class="btn btn-primary" href="#/filmes/${destaque.id}">Ver detalhes</a>
          <button class="btn btn-secondary" data-avaliar>Avaliar filme</button>
        </div>
      </div>
      ${poster(destaque)}
    </section>
    <section class="section">
      <div class="section-head">
        <div>
          <h2 class="h2">Catálogo</h2>
          <span class="muted" data-contagem></span>
        </div>
        <label class="search">${icone.busca}<input type="search" placeholder="Buscar por título…" aria-label="Buscar por título" data-busca></label>
      </div>
      <div class="chips" role="group" aria-label="Filtrar por categoria">
        <button class="chip active" data-cat="">Todos</button>
        ${categorias.map((c) => `<button class="chip" data-cat="${c.id}">${esc(c.nome)}</button>`).join('')}
      </div>
      <div class="grid" data-grade></div>
    </section>`;

  // "Avaliar filme" leva direto ao formulário de avaliação no detalhe.
  $('[data-avaliar]', raiz).addEventListener('click', () => {
    try { sessionStorage.setItem('carboflix.focarAvaliacao', '1'); } catch { /* sem armazenamento */ }
    ctx.navegar(`#/filmes/${destaque.id}`);
  });

  function desenharGrade() {
    const termo = busca.trim().toLocaleLowerCase('pt-BR');
    const visiveis = filmes.filter((f) =>
      (!categoriaId || f.categorias.some((c) => c.id === categoriaId)) &&
      (!termo || f.titulo.toLocaleLowerCase('pt-BR').includes(termo)));
    $('[data-contagem]', raiz).textContent = plural(visiveis.length, 'filme', 'filmes');
    $('[data-grade]', raiz).innerHTML = visiveis.length
      ? visiveis.map((f) => `
        <a class="card" href="#/filmes/${f.id}">
          ${poster(f)}
          <span class="card-title">${esc(f.titulo)}</span>
          <span class="card-meta">${metaFilme(f)}</span>
          <span class="rating">${icone.estrela}${f.totalAvaliacoes ? `<strong>${formatarMedia(f.mediaAvaliacoes)}</strong><span class="muted">(${plural(f.totalAvaliacoes, 'avaliação', 'avaliações')})</span>` : '<span class="muted">Sem avaliações</span>'}</span>
        </a>`).join('')
      : '<div class="empty" style="grid-column:1/-1"><strong>Nenhum filme encontrado.</strong>Tente outra categoria ou outro termo de busca.</div>';
  }

  $$('[data-cat]', raiz).forEach((b) => b.addEventListener('click', () => {
    categoriaId = b.dataset.cat ? Number(b.dataset.cat) : null;
    $$('[data-cat]', raiz).forEach((x) => x.classList.toggle('active', x === b));
    desenharGrade();
  }));
  $('[data-busca]', raiz).addEventListener('input', (e) => { busca = e.target.value; desenharGrade(); });

  desenharGrade();
  document.title = 'Catálogo · CarboFlix';
}
