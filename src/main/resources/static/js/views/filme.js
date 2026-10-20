// Tela 06 Detalhe do filme do protótipo: dados do filme e CRUD de /avaliacoes.
import { api } from '../api.js';
import { sessao } from '../session.js';
import { $, $$, avatar, comCarregamento, confirmar, esc, estrelas, formatarData, formatarMedia, icone, mostrarErro, plural, poster, textoClassificacao, toast, valorOuNulo } from '../ui.js';
import { abrirFormularioFilme } from './filme-form.js';

export async function render(raiz, ctx) {
  const id = Number(ctx.params[0]);
  const [filme, todas, meusPerfis] = await Promise.all([api.filme(id), api.avaliacoes(), api.perfis()]);
  if (!ctx.ehAtual()) return;

  const idsMeusPerfis = new Set(meusPerfis.map((p) => p.id));
  let perfil = sessao.perfil && meusPerfis.find((p) => p.id === sessao.perfil.id);
  if (sessao.perfil && !perfil) sessao.esquecerPerfil();

  const avaliacoes = todas.filter((a) => a.filmeId === id).sort((a, b) => new Date(b.criadoEm) - new Date(a.criadoEm));
  const doPerfilAtivo = perfil && avaliacoes.find((a) => a.perfilId === perfil.id);
  // Avaliação sendo editada no formulário (null = criando uma nova com o perfil ativo).
  let editando = doPerfilAtivo || null;
  let nota = editando?.nota || 0;

  raiz.innerHTML = `
    <div class="detail">
      <a class="back" href="#/catalogo">${icone.voltar} Voltar ao catálogo</a>
      <div class="detail-top">
        ${poster(filme)}
        <div class="detail-info">
          <h1>${esc(filme.titulo)}</h1>
          <div class="meta-line muted">
            <span>${filme.anoLancamento}</span><span>${filme.duracaoMin} min</span>
            <span class="badge-class" title="${esc(textoClassificacao(filme.classificacaoIndicativa))}">${esc(filme.classificacaoIndicativa)}</span>
          </div>
          ${filme.categorias.length ? `<div class="chips">${filme.categorias.map((c) => `<span class="chip">${esc(c.nome)}</span>`).join('')}</div>` : ''}
          <div class="score surface">
            <span class="value">${formatarMedia(filme.mediaAvaliacoes)}</span>
            <div class="stack" style="gap:6px">${estrelas(filme.mediaAvaliacoes)}<span class="muted" style="font-size:13px">${filme.totalAvaliacoes ? `Média de ${plural(filme.totalAvaliacoes, 'avaliação', 'avaliações')}` : 'Ainda sem avaliações'}</span></div>
          </div>
          ${filme.sinopse ? `<div class="stack" style="gap:8px"><strong style="font-size:16px">Sinopse</strong><p>${esc(filme.sinopse)}</p></div>` : ''}
          <div class="row">
            <button class="btn btn-secondary" data-editar-filme>Editar filme</button>
            <button class="btn btn-ghost" data-excluir-filme>Excluir</button>
          </div>
        </div>
      </div>

      <section class="stack" style="gap:18px">
        <div class="row"><h2 class="h2">Avaliações</h2><span class="muted" style="font-size:16px">${avaliacoes.length}</span></div>
        <div class="reviews">
          <div class="surface pad stack" data-formulario></div>
          <div class="stack" style="gap:12px" data-lista></div>
        </div>
      </section>
    </div>`;

  // ---------- Formulário de avaliação ----------
  function desenharFormulario() {
    const caixa = $('[data-formulario]', raiz);
    const autor = editando ? meusPerfis.find((p) => p.id === editando.perfilId) : perfil;
    if (!autor) {
      caixa.innerHTML = `
        <strong style="font-size:16px">Quer avaliar este filme?</strong>
        <p class="muted" style="margin:0">Escolha um perfil primeiro. Cada perfil tem as próprias avaliações.</p>
        <a class="btn btn-primary" href="#/perfis">Escolher perfil</a>`;
      return;
    }
    caixa.innerHTML = `
      <div class="row" style="gap:10px">
        ${avatar(autor.nomePerfil, autor.avatarUrl, autor.id, 'avatar-sm')}
        <strong style="font-size:16px">${editando ? 'Sua avaliação como' : 'Avaliar como'} ${esc(autor.nomePerfil)}</strong>
      </div>
      <form class="stack" novalidate>
        <div class="field" data-campo="nota">
          <span class="field-label">Sua nota</span>
          <div class="star-picker" role="radiogroup" aria-label="Nota de 1 a 5">
            ${[1, 2, 3, 4, 5].map((n) => `<button type="button" data-nota="${n}" role="radio" aria-label="${n} ${n === 1 ? 'estrela' : 'estrelas'}">${icone.estrela}</button>`).join('')}
          </div>
          <span class="field-error">${icone.alerta}<span></span></span>
        </div>
        <div class="field" data-campo="comentario">
          <label for="f-comentario">Comentário (opcional)</label>
          <textarea class="input" id="f-comentario" name="comentario" placeholder="O que você achou?">${esc(editando?.comentario || '')}</textarea>
          <span class="field-error">${icone.alerta}<span></span></span>
        </div>
        <div class="alert" hidden data-erro></div>
        <button type="submit" class="btn btn-primary btn-block" data-salvar>${editando ? 'Salvar alterações' : 'Publicar avaliação'}</button>
        ${editando ? '<button type="button" class="btn btn-ghost btn-block" data-excluir-minha>Excluir minha avaliação</button>' : ''}
        <span class="hint">Cada perfil pode avaliar um filme uma única vez.</span>
      </form>`;

    const form = $('form', caixa);
    const pintar = (valor) => $$('[data-nota]', form).forEach((b) => {
      const on = Number(b.dataset.nota) <= valor;
      b.classList.toggle('on', on);
      b.setAttribute('aria-checked', Number(b.dataset.nota) === nota);
    });
    $$('[data-nota]', form).forEach((b) => {
      b.addEventListener('click', () => { nota = Number(b.dataset.nota); pintar(nota); });
      b.addEventListener('mouseenter', () => pintar(Number(b.dataset.nota)));
    });
    $('.star-picker', form).addEventListener('mouseleave', () => pintar(nota));
    pintar(nota);

    form.addEventListener('submit', async (e) => {
      e.preventDefault();
      const comentario = valorOuNulo(form.comentario.value);
      await comCarregamento($('[data-salvar]', form), async () => {
        try {
          if (editando) await api.atualizarAvaliacao(editando.id, { nota: nota || null, comentario });
          else await api.criarAvaliacao({ perfilId: perfil.id, filmeId: id, nota: nota || null, comentario });
          toast(editando ? 'Avaliação atualizada.' : 'Avaliação publicada!');
          ctx.recarregar();
        } catch (erro) {
          mostrarErro(form, erro);
        }
      });
    });
    $('[data-excluir-minha]', form)?.addEventListener('click', () => excluirAvaliacao(editando));
  }

  async function excluirAvaliacao(avaliacao) {
    const ok = await confirmar({
      titulo: 'Excluir avaliação?',
      mensagem: `A avaliação de ${avaliacao.nomePerfil} para "${filme.titulo}" será removida.`,
      acao: () => api.excluirAvaliacao(avaliacao.id),
    });
    if (ok) { toast('Avaliação excluída.'); ctx.recarregar(); }
  }

  // ---------- Lista ----------
  $('[data-lista]', raiz).innerHTML = avaliacoes.length
    ? avaliacoes.map((a) => {
      const minha = idsMeusPerfis.has(a.perfilId);
      return `
        <article class="review surface ${minha ? 'mine' : ''}">
          ${avatar(a.nomePerfil, null, a.perfilId)}
          <div class="review-body">
            <div class="review-head"><strong>${esc(a.nomePerfil)}</strong>${estrelas(a.nota)}<span class="muted" style="font-size:12px">${formatarData(a.criadoEm)}</span></div>
            ${a.comentario ? `<p>${esc(a.comentario)}</p>` : '<p><em>Sem comentário.</em></p>'}
          </div>
        </article>
        ${minha ? `<div class="review-actions"><button class="btn btn-ghost btn-sm" data-editar="${a.id}">Editar</button><button class="btn btn-ghost btn-sm" data-excluir="${a.id}">Excluir</button></div>` : ''}`;
    }).join('')
    : '<div class="empty"><strong>Ninguém avaliou este filme ainda.</strong>Seja o primeiro!</div>';

  $$('[data-editar]', raiz).forEach((b) => b.addEventListener('click', () => {
    editando = avaliacoes.find((a) => a.id === Number(b.dataset.editar));
    nota = editando.nota;
    desenharFormulario();
    $('[data-formulario]', raiz).scrollIntoView({ behavior: 'smooth', block: 'center' });
  }));
  $$('[data-excluir]', raiz).forEach((b) => b.addEventListener('click', () => excluirAvaliacao(avaliacoes.find((a) => a.id === Number(b.dataset.excluir)))));

  // ---------- Ações do filme ----------
  $('[data-editar-filme]', raiz).addEventListener('click', async () => {
    if (await abrirFormularioFilme(filme)) ctx.recarregar();
  });
  $('[data-excluir-filme]', raiz).addEventListener('click', async () => {
    const ok = await confirmar({
      titulo: `Excluir "${filme.titulo}"?`,
      mensagem: 'O filme sai do catálogo. Filmes que já têm avaliações não podem ser excluídos.',
      confirmar: 'Excluir filme',
      acao: () => api.excluirFilme(filme.id),
    });
    if (ok) { toast('Filme excluído.'); ctx.navegar('#/catalogo'); }
  });

  desenharFormulario();
  document.title = `${filme.titulo} · CarboFlix`;

  let focar = false;
  try { focar = sessionStorage.getItem('carboflix.focarAvaliacao') === '1'; sessionStorage.removeItem('carboflix.focarAvaliacao'); } catch { /* sem armazenamento */ }
  if (focar) $('[data-formulario]', raiz).scrollIntoView({ behavior: 'smooth', block: 'center' });
}
