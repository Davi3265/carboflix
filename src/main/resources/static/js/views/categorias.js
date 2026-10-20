// Tela 09 Categorias do protótipo (CRUD de /categorias).
import { api } from '../api.js';
import { $, $$, campo, comCarregamento, confirmar, esc, icone, limparErros, mostrarErro, plural, toast, valorOuNulo } from '../ui.js';

export async function render(raiz, ctx) {
  const [categorias, filmes] = await Promise.all([api.categorias(), api.filmes()]);
  if (!ctx.ehAtual()) return;
  categorias.sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR'));
  const quantos = (id) => filmes.filter((f) => f.categorias.some((c) => c.id === id)).length;
  let editando = null;

  raiz.innerHTML = `
    <div class="page">
      <div class="page-head">
        <div>
          <h1 class="h1">Categorias</h1>
          <p>Organize o catálogo. Uma categoria com filmes vinculados não pode ser excluída.</p>
        </div>
      </div>
      <div class="cat-layout">
        <div class="cat-grid" data-lista></div>
        <form class="surface pad stack sticky" novalidate data-form></form>
      </div>
    </div>`;

  function desenharLista() {
    $('[data-lista]', raiz).innerHTML = categorias.length
      ? categorias.map((c) => `
        <article class="cat-card surface ${editando?.id === c.id ? 'editing' : ''}">
          <header>
            <h3>${esc(c.nome)}</h3>
            <button class="icon-btn" data-editar="${c.id}" title="Editar" aria-label="Editar ${esc(c.nome)}">${icone.editar}</button>
            <button class="icon-btn danger" data-excluir="${c.id}" title="Excluir" aria-label="Excluir ${esc(c.nome)}">${icone.lixeira}</button>
          </header>
          ${c.descricao ? `<p>${esc(c.descricao)}</p>` : '<p><em>Sem descrição.</em></p>'}
          <span class="cat-count">${plural(quantos(c.id), 'filme', 'filmes')}</span>
        </article>`).join('')
      : '<div class="empty" style="grid-column:1/-1"><strong>Nenhuma categoria ainda.</strong>Crie a primeira no formulário ao lado.</div>';

    $$('[data-editar]', raiz).forEach((b) => b.addEventListener('click', () => {
      editando = categorias.find((c) => c.id === Number(b.dataset.editar));
      desenharLista();
      desenharFormulario();
    }));
    $$('[data-excluir]', raiz).forEach((b) => b.addEventListener('click', async () => {
      const cat = categorias.find((c) => c.id === Number(b.dataset.excluir));
      const ok = await confirmar({
        titulo: `Excluir a categoria ${cat.nome}?`,
        mensagem: quantos(cat.id) ? `Ela está em ${plural(quantos(cat.id), 'filme', 'filmes')}; a API vai recusar a exclusão.` : 'Essa ação não pode ser desfeita.',
        confirmar: 'Excluir categoria',
        acao: () => api.excluirCategoria(cat.id),
      });
      if (ok) { toast('Categoria excluída.'); ctx.recarregar(); }
    }));
  }

  function desenharFormulario() {
    const form = $('[data-form]', raiz);
    form.innerHTML = `
      <h2 class="h3">${editando ? 'Editar categoria' : 'Nova categoria'}</h2>
      ${campo({ nome: 'nome', rotulo: 'Nome', valor: editando?.nome, placeholder: 'Ex.: Drama', obrigatorio: true })}
      ${campo({ nome: 'descricao', rotulo: 'Descrição', tipo: 'textarea', valor: editando?.descricao, placeholder: 'Opcional' })}
      <div class="alert" hidden data-erro></div>
      <button type="submit" class="btn btn-primary btn-block" data-salvar>${editando ? 'Salvar alterações' : 'Salvar categoria'}</button>
      ${editando ? '<button type="button" class="btn btn-ghost btn-block" data-cancelar>Cancelar edição</button>' : ''}`;
    limparErros(form);
    $('[data-cancelar]', form)?.addEventListener('click', () => { editando = null; desenharLista(); desenharFormulario(); });
    if (editando) form.nome.focus();
  }

  $('[data-form]', raiz).addEventListener('submit', async (e) => {
    e.preventDefault();
    const form = e.currentTarget;
    const dados = { nome: form.nome.value.trim(), descricao: valorOuNulo(form.descricao.value) };
    await comCarregamento($('[data-salvar]', form), async () => {
      try {
        if (editando) await api.atualizarCategoria(editando.id, dados);
        else await api.criarCategoria(dados);
        toast(editando ? 'Categoria atualizada.' : `Categoria ${dados.nome} criada.`);
        ctx.recarregar();
      } catch (erro) {
        mostrarErro(form, erro);
      }
    });
  });

  desenharLista();
  desenharFormulario();
  document.title = 'Categorias · CarboFlix';
}
