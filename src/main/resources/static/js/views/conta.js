// Tela 10 Minha conta do protótipo (GET, PUT e DELETE /usuarios/{id}).
import { api } from '../api.js';
import { sessao } from '../session.js';
import { $, campo, comCarregamento, confirmar, mostrarErro, toast, valorOuNulo } from '../ui.js';

export async function render(raiz, ctx) {
  const usuario = await api.minhaConta();
  if (!ctx.ehAtual()) return;
  sessao.definirUsuario(usuario);

  raiz.innerHTML = `
    <div class="page">
      <div class="narrow stack" style="gap:24px">
        <div class="page-head"><div><h1 class="h1">Minha conta</h1><p>Atualize seus dados de acesso.</p></div></div>
        <form class="surface stack" style="padding:28px;gap:18px" novalidate>
          ${campo({ nome: 'nome', rotulo: 'Nome', valor: usuario.nome, obrigatorio: true, extra: 'autocomplete="name"' })}
          ${campo({ nome: 'email', rotulo: 'E-mail', tipo: 'email', valor: usuario.email, obrigatorio: true, extra: 'autocomplete="email"' })}
          ${campo({ nome: 'senha', rotulo: 'Nova senha (opcional)', tipo: 'password', placeholder: 'Deixe em branco para manter a atual', extra: 'autocomplete="new-password"' })}
          <div class="alert" hidden data-erro></div>
          <div class="row" style="justify-content:flex-end"><button type="submit" class="btn btn-primary" data-salvar>Salvar alterações</button></div>
        </form>
        <div class="danger-zone">
          <div><strong style="font-size:16px">Excluir conta</strong><p>Remove sua conta de forma permanente. Exclua seus perfis antes.</p></div>
          <button class="btn btn-danger" data-excluir>Excluir conta</button>
        </div>
      </div>
    </div>`;

  const form = $('form', raiz);
  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const dados = { nome: form.nome.value.trim(), email: form.email.value.trim(), senha: valorOuNulo(form.senha.value) };
    await comCarregamento($('[data-salvar]', form), async () => {
      try {
        const atualizado = await api.atualizarConta(usuario.id, dados);
        // O token JWT guarda o e-mail: se ele mudou, é preciso entrar de novo.
        if (atualizado.email !== usuario.email) {
          ctx.sair();
          toast('E-mail alterado. Entre novamente com o novo e-mail.');
          return;
        }
        sessao.definirUsuario(atualizado);
        toast('Dados atualizados.');
        ctx.recarregar();
      } catch (erro) {
        mostrarErro(form, erro);
      }
    });
  });

  $('[data-excluir]', raiz).addEventListener('click', async () => {
    const ok = await confirmar({
      titulo: 'Excluir sua conta?',
      mensagem: 'Sua conta será removida de forma permanente. Se ainda houver perfis, a API vai recusar.',
      confirmar: 'Excluir conta',
      acao: () => api.excluirConta(usuario.id),
    });
    if (ok) { ctx.sair(); toast('Conta excluída.'); }
  });

  document.title = 'Minha conta · CarboFlix';
}
