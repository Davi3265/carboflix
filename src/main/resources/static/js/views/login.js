// Telas 01 Login e 02 Cadastro do protótipo (POST /auth/login e /auth/register).
import { api } from '../api.js';
import { sessao } from '../session.js';
import { $, $$, campo, comCarregamento, limparErros, logo, mostrarErro, toast } from '../ui.js';

const POSTERES = [['#3B1D60', '#120A1F'], ['#0F4C5C', '#081A20'], ['#7A1F1F', '#1A0A0A'], ['#1F3B7A', '#0A1020']];

export async function render(raiz, ctx) {
  let modo = 'entrar';

  raiz.innerHTML = `
    <div class="auth">
      <section class="auth-brand">
        ${logo()}
        <div>
          <h1>Seu catálogo de filmes, do seu jeito.</h1>
          <p>Crie perfis para a família, descubra filmes por categoria e compartilhe sua opinião com avaliações de 1 a 5 estrelas.</p>
        </div>
        <div class="auth-posters">${POSTERES.map(([a, b], i) => `<span style="background:linear-gradient(135deg,${a},${b});opacity:${0.9 - i * 0.15}"></span>`).join('')}</div>
      </section>
      <section class="auth-form">
        <form class="auth-card surface" novalidate>
          <div class="tabs" role="tablist">
            <button type="button" data-modo="entrar">Entrar</button>
            <button type="button" data-modo="cadastrar">Criar conta</button>
          </div>
          <div>
            <h2 data-titulo></h2>
            <p class="muted" style="margin:0" data-subtitulo></p>
          </div>
          <div class="stack" data-campos></div>
          <div class="alert" hidden data-erro></div>
          <button class="btn btn-primary btn-block" type="submit" data-enviar></button>
          <p class="auth-foot" data-rodape></p>
        </form>
      </section>
    </div>`;

  const form = $('form', raiz);

  function desenhar() {
    const cadastro = modo === 'cadastrar';
    $$('[data-modo]', form).forEach((b) => b.classList.toggle('active', b.dataset.modo === modo));
    $('[data-titulo]', form).textContent = cadastro ? 'Crie sua conta' : 'Bem-vindo de volta';
    $('[data-subtitulo]', form).textContent = cadastro ? 'Leva menos de um minuto. Depois é só criar seus perfis.' : 'Entre com seu e-mail e senha para continuar.';
    $('[data-campos]', form).innerHTML = [
      cadastro ? campo({ nome: 'nome', rotulo: 'Nome', placeholder: 'Seu nome', extra: 'autocomplete="name"' }) : '',
      campo({ nome: 'email', rotulo: 'E-mail', tipo: 'email', placeholder: 'voce@email.com', extra: 'autocomplete="email"' }),
      campo({ nome: 'senha', rotulo: 'Senha', tipo: 'password', placeholder: cadastro ? 'Mínimo de 6 caracteres' : 'Sua senha', extra: `autocomplete="${cadastro ? 'new-password' : 'current-password'}"` }),
    ].join('');
    $('[data-enviar]', form).textContent = cadastro ? 'Criar conta' : 'Entrar';
    $('[data-rodape]', form).innerHTML = cadastro
      ? 'Já tem conta? <button type="button" class="link-btn" data-trocar>Entrar</button>'
      : 'Ainda não tem conta? <button type="button" class="link-btn" data-trocar>Criar conta</button>';
    $('[data-trocar]', form).addEventListener('click', () => { modo = cadastro ? 'entrar' : 'cadastrar'; desenhar(); });
    limparErros(form);
    $('input', form).focus();
  }

  $$('[data-modo]', form).forEach((b) => b.addEventListener('click', () => { modo = b.dataset.modo; desenhar(); }));

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const dados = Object.fromEntries(new FormData(form));
    await comCarregamento($('[data-enviar]', form), async () => {
      try {
        if (modo === 'cadastrar') {
          await api.registrar({ nome: dados.nome, email: dados.email, senha: dados.senha });
          toast(`Conta criada. Boas-vindas, ${dados.nome}!`);
        }
        const { token } = await api.login(dados.email, dados.senha);
        sessao.entrar(token);
        sessao.definirUsuario(await api.minhaConta());
        ctx.navegar('#/perfis');
      } catch (erro) {
        mostrarErro(form, erro);
      }
    });
  });

  desenhar();
  document.title = 'Entrar · CarboFlix';
}
