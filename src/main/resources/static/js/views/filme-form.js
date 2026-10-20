// Tela 08 Formulário de filme do protótipo (POST e PUT /filmes), aberta como modal.
import { api } from '../api.js';
import { $, $$, abrirModal, campo, comCarregamento, esc, icone, mostrarErro, numeroOuNulo, toast, valorOuNulo } from '../ui.js';

const CLASSIFICACOES = [['L', 'Livre'], ['10', '10 anos'], ['12', '12 anos'], ['14', '14 anos'], ['16', '16 anos'], ['18', '18 anos']];

// Resolve com o filme salvo, ou null se o usuário cancelar.
export async function abrirFormularioFilme(filme) {
  const categorias = (await api.categorias()).sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR'));
  const selecionadas = new Set((filme?.categorias || []).map((c) => c.id));

  return new Promise((resolve) => {
    const { el, fechar } = abrirModal(`
      <div>
        <h2>${filme ? 'Editar filme' : 'Novo filme'}</h2>
        <span class="muted" style="font-size:13px">Campos com * são obrigatórios.</span>
      </div>
      <form class="stack" novalidate>
        ${campo({ nome: 'titulo', rotulo: 'Título', valor: filme?.titulo, placeholder: 'Digite o título do filme', obrigatorio: true })}
        <div class="form-grid">
          ${campo({ nome: 'anoLancamento', rotulo: 'Ano de lançamento', tipo: 'number', valor: filme?.anoLancamento, placeholder: '2024', obrigatorio: true, extra: 'min="1888" max="2100"' })}
          ${campo({ nome: 'duracaoMin', rotulo: 'Duração (min)', tipo: 'number', valor: filme?.duracaoMin, placeholder: '120', obrigatorio: true, extra: 'min="1"' })}
          <div class="field" data-campo="classificacaoIndicativa">
            <label for="f-classificacao">Classificação *</label>
            <select class="input" id="f-classificacao" name="classificacaoIndicativa">
              <option value="">Selecione</option>
              ${CLASSIFICACOES.map(([v, t]) => `<option value="${v}" ${filme?.classificacaoIndicativa === v ? 'selected' : ''}>${t}</option>`).join('')}
            </select>
            <span class="field-error">${icone.alerta}<span></span></span>
          </div>
        </div>
        ${campo({ nome: 'posterUrl', rotulo: 'URL do pôster', valor: filme?.posterUrl, placeholder: 'https://…' })}
        ${campo({ nome: 'sinopse', rotulo: 'Sinopse', tipo: 'textarea', valor: filme?.sinopse, placeholder: 'Sobre o que é o filme?' })}
        <div class="field" data-campo="categoriaIds">
          <span class="field-label">Categorias</span>
          <div class="chips">
            ${categorias.length
              ? categorias.map((c) => `<button type="button" class="chip ${selecionadas.has(c.id) ? 'active' : ''}" data-cat="${c.id}" aria-pressed="${selecionadas.has(c.id)}">${esc(c.nome)}</button>`).join('')
              : '<span class="hint">Nenhuma categoria cadastrada ainda. Crie em Categorias.</span>'}
          </div>
          <span class="field-error">${icone.alerta}<span></span></span>
        </div>
        <div class="alert" hidden data-erro></div>
        <div class="modal-actions">
          <button type="button" class="btn btn-ghost" data-fechar>Cancelar</button>
          <button type="submit" class="btn btn-primary" data-salvar>Salvar filme</button>
        </div>
      </form>`, { largo: true });

    const form = $('form', el);
    $$('[data-cat]', form).forEach((b) => b.addEventListener('click', () => {
      const id = Number(b.dataset.cat);
      if (selecionadas.has(id)) selecionadas.delete(id); else selecionadas.add(id);
      b.classList.toggle('active', selecionadas.has(id));
      b.setAttribute('aria-pressed', selecionadas.has(id));
    }));
    $$('[data-fechar]', el).forEach((b) => b.addEventListener('click', () => resolve(null)));

    form.addEventListener('submit', async (e) => {
      e.preventDefault();
      const dados = {
        titulo: form.titulo.value.trim(),
        anoLancamento: numeroOuNulo(form.anoLancamento.value),
        duracaoMin: numeroOuNulo(form.duracaoMin.value),
        classificacaoIndicativa: form.classificacaoIndicativa.value,
        posterUrl: valorOuNulo(form.posterUrl.value),
        sinopse: valorOuNulo(form.sinopse.value),
        categoriaIds: [...selecionadas],
      };
      await comCarregamento($('[data-salvar]', form), async () => {
        try {
          const salvo = filme ? await api.atualizarFilme(filme.id, dados) : await api.criarFilme(dados);
          toast(filme ? 'Filme atualizado.' : `${salvo.titulo} adicionado ao catálogo.`);
          fechar();
          resolve(salvo);
        } catch (erro) {
          mostrarErro(form, erro);
        }
      });
    });
  });
}
