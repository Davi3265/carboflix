// Telas 03 Perfis e 04 Editar perfil do protótipo (CRUD de /perfis).
import { api } from '../api.js';
import { sessao } from '../session.js';
import { $, $$, abrirModal, avatar, campo, comCarregamento, confirmar, esc, icone, logo, mostrarErro, toast, valorOuNulo } from '../ui.js';

export async function render(raiz, ctx) {
  let editando = false;
  let lista = await api.perfis();

  function desenhar() {
    raiz.innerHTML = `
      <div class="profiles-page">
        <div class="row">${logo()}<span class="spacer"></span><button class="btn btn-ghost btn-sm" data-sair>${icone.sair} Sair</button></div>
        <div class="profiles-center">
          <div>
            <h1>${editando ? 'Gerenciar perfis' : lista.length ? 'Quem está assistindo?' : 'Crie seu primeiro perfil'}</h1>
            <p class="muted" style="margin:0;font-size:16px">${editando ? 'Clique em um perfil para editar ou excluir.' : lista.length ? 'Escolha um perfil para ver o catálogo e avaliar filmes.' : 'Cada pessoa da casa pode ter o seu, com avaliações separadas.'}</p>
          </div>
          <div class="profiles-grid ${editando ? 'editing' : ''}">
            ${lista.map((p) => `
              <button class="profile-tile" data-id="${p.id}">
                ${avatar(p.nomePerfil, p.avatarUrl, p.id)}
                <span class="name">${esc(p.nomePerfil)}</span>
                <span class="type">${p.tipo}</span>
              </button>`).join('')}
            <button class="profile-tile profile-add" data-novo>
              <span class="box">${icone.mais}</span>
              <span class="name">Adicionar perfil</span>
            </button>
          </div>
          ${lista.length ? `<button class="btn ${editando ? 'btn-primary' : 'btn-secondary'}" data-gerenciar>${editando ? 'Concluído' : 'Gerenciar perfis'}</button>` : ''}
        </div>
      </div>`;

    $('[data-sair]', raiz).addEventListener('click', () => ctx.sair());
    $('[data-novo]', raiz).addEventListener('click', () => formulario());
    $('[data-gerenciar]', raiz)?.addEventListener('click', () => { editando = !editando; desenhar(); });
    $$('.profile-tile[data-id]', raiz).forEach((b) => b.addEventListener('click', () => {
      const perfil = lista.find((p) => p.id === Number(b.dataset.id));
      if (editando) formulario(perfil);
      else { sessao.escolherPerfil(perfil); ctx.navegar('#/catalogo'); }
    }));
  }

  function formulario(perfil) {
    let tipo = perfil?.tipo || 'ADULTO';
    const { el, fechar } = abrirModal(`
      <h2>${perfil ? 'Editar perfil' : 'Novo perfil'}</h2>
      <form class="stack" novalidate>
        ${campo({ nome: 'nomePerfil', rotulo: 'Nome do perfil', valor: perfil?.nomePerfil, placeholder: 'Ex.: Ana', obrigatorio: true })}
        ${campo({ nome: 'avatarUrl', rotulo: 'URL do avatar (opcional)', valor: perfil?.avatarUrl, placeholder: 'https://…' })}
        <div class="field" data-campo="tipo">
          <span class="field-label">Tipo do perfil</span>
          <div class="segmented">
            <button type="button" data-tipo="ADULTO">Adulto</button>
            <button type="button" data-tipo="INFANTIL">Infantil</button>
          </div>
          <span class="field-error">${icone.alerta}<span></span></span>
        </div>
        <div class="alert" hidden data-erro></div>
        <div class="modal-actions">
          ${perfil ? '<button type="button" class="btn btn-danger" data-excluir>Excluir perfil</button><span class="spacer"></span>' : ''}
          <button type="button" class="btn btn-ghost" data-fechar>Cancelar</button>
          <button type="submit" class="btn btn-primary" data-salvar>Salvar perfil</button>
        </div>
      </form>`);

    const form = $('form', el);
    const marcarTipo = () => $$('[data-tipo]', form).forEach((b) => b.classList.toggle('active', b.dataset.tipo === tipo));
    $$('[data-tipo]', form).forEach((b) => b.addEventListener('click', () => { tipo = b.dataset.tipo; marcarTipo(); }));
    marcarTipo();

    form.addEventListener('submit', async (e) => {
      e.preventDefault();
      const dados = { nomePerfil: form.nomePerfil.value.trim(), avatarUrl: valorOuNulo(form.avatarUrl.value), tipo };
      await comCarregamento($('[data-salvar]', form), async () => {
        try {
          const salvo = perfil ? await api.atualizarPerfil(perfil.id, dados) : await api.criarPerfil(dados);
          if (sessao.perfil?.id === salvo.id) sessao.escolherPerfil(salvo);
          toast(perfil ? 'Perfil atualizado.' : `Perfil ${salvo.nomePerfil} criado.`);
          fechar();
          lista = await api.perfis();
          desenhar();
        } catch (erro) {
          mostrarErro(form, erro);
        }
      });
    });

    $('[data-excluir]', form)?.addEventListener('click', async () => {
      fechar();
      const excluiu = await confirmar({
        titulo: `Excluir o perfil ${perfil.nomePerfil}?`,
        mensagem: 'Essa ação não pode ser desfeita.',
        confirmar: 'Excluir perfil',
        acao: () => api.excluirPerfil(perfil.id),
      });
      if (!excluiu) return;
      if (sessao.perfil?.id === perfil.id) sessao.esquecerPerfil();
      toast('Perfil excluído.');
      lista = await api.perfis();
      desenhar();
    });
  }

  desenhar();
  document.title = 'Perfis · CarboFlix';
}
