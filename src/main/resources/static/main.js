const API_BASE_URL = 'http://localhost:8080/api';
let itensVenda = [];

function formatCurrencyBRL(value) {
    if (typeof value !== 'number') {
        value = parseFloat(value);
    }
    if (isNaN(value)) {
        return '0,00';
    }
    return value.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

async function makeRequest(url, options) {
    try {
        const response = await fetch(url, options);
        if (!response.ok) {
            const error = await response.json();
            showMessage(error.message || `Erro na requisição: ${response.status}`, 'error');
            return null;
        }
        if (options.method === 'DELETE') {
            return true;
        }
        return await response.json();
    } catch (error) {
        showMessage(`Erro de conexão: ${error.message}`, 'error');
        return null;
    }
}

function showMessage(message, type) {
    const messageDiv = document.getElementById('message');
    if (messageDiv) {
        messageDiv.textContent = message;
        messageDiv.className = `output ${type}`;
        setTimeout(() => {
            messageDiv.textContent = '';
            messageDiv.className = 'output';
        }, 5000);
    }
}

function injectStyles() {
    const style = document.createElement('style');
    style.textContent = `
        .venda-selecionada {
            background-color: #f0f8ff !important;
        }
        #form-venda-create button {
            width: fit-content;
            padding: 10px 20px;
            font-size: 16px;
        }
        .search-container input {
            width: 300px;
        }
        .itens-detalhe-table {
            background-color: #f7f7f7; /* Tom mais claro para a tabela de itens */
        }
    `;
    document.head.appendChild(style);
}

document.addEventListener('DOMContentLoaded', () => {
    injectStyles();

    if (document.getElementById('form-produto-create')) {
        listProdutos();
        document.getElementById('form-produto-create').addEventListener('submit', createProduto);
        document.getElementById('produto-filter-input').addEventListener('input', (e) => {
            const query = e.target.value;
            listProdutos(query);
        });
    }
    if (document.getElementById('form-fornecedor-create')) {
        listFornecedores();
        document.getElementById('form-fornecedor-create').addEventListener('submit', createFornecedor);
    }
    if (document.getElementById('form-cliente-create')) {
        listClientes();
        document.getElementById('form-cliente-create').addEventListener('submit', createCliente);
        const editClientForm = document.getElementById('edit-client-form');
        if (editClientForm) {
            editClientForm.addEventListener('submit', updateCliente);
        }
    }
    if (document.getElementById('form-venda-create')) {
        listVendas();
        setupSearch('cliente-search-input', 'venda-cliente-id', 'cliente-search-results', 'clientes');
        setupSearch('produto-search-input', 'venda-produto-id', 'produto-search-results', 'produtos', true);
        document.getElementById('form-venda-create').addEventListener('submit', registrarVenda);
    }
});

// PRODUTO functions
async function createProduto(event) {
    event.preventDefault();
    const nome = document.getElementById('produto-nome').value;
    const descricao = document.getElementById('produto-descricao').value;
    const quantidade = document.getElementById('produto-quantidade').value;
    const precoDeCusto = document.getElementById('produto-preco-custo').value;
    const valorDeVenda = document.getElementById('produto-valor-venda').value;

    const payload = {
        nome,
        descricao,
        quantidade: parseInt(quantidade),
        precoDeCusto: parseFloat(precoDeCusto),
        valorDeVenda: parseFloat(valorDeVenda)
    };

    const produto = await makeRequest(`${API_BASE_URL}/produtos`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });
    if (produto) {
        showMessage('Produto criado com sucesso!', 'success');
        listProdutos();
        document.getElementById('form-produto-create').reset();
    }
}

async function listProdutos(query = '') {
    let url = `${API_BASE_URL}/produtos`;
    if (query.length > 0) {
        url = `${API_BASE_URL}/produtos/search?nome=${encodeURIComponent(query)}`;
    }

    const produtos = await makeRequest(url, { method: 'GET' });
    const outputDiv = document.getElementById('output-produto-list');

    if (produtos && outputDiv) {
        let tableHtml = '<table><thead><tr><th>ID</th><th>Nome</th><th>Descrição</th><th>Qtd</th><th style="text-align: center; width: 120px;">Preço de Custo</th><th style="text-align: center; width: 120px;">Valor de Venda</th><th>Ações</th></tr></thead><tbody>';
        produtos.forEach(produto => {
            tableHtml += `
                <tr>
                    <td>${produto.id}</td>
                    <td>${produto.nome}</td>
                    <td>${produto.descricao || ''}</td>
                    <td>${produto.quantidade}</td>
                    <td><div style="display: flex; justify-content: space-between; align-items: center; padding: 0 5px;"><span>R$</span><span>${formatCurrencyBRL(produto.precoDeCusto)}</span></div></td>
                    <td><div style="display: flex; justify-content: space-between; align-items: center; padding: 0 5px;"><span>R$</span><span>${formatCurrencyBRL(produto.valorDeVenda)}</span></div></td>
                    <td>
                        <button onclick="editProduto(${produto.id})">Editar</button>
                        <button class="delete-button" onclick="deleteProduto(${produto.id})">Excluir</button>
                    </td>
                </tr>
            `;
        });
        tableHtml += '</tbody></table>';
        outputDiv.innerHTML = tableHtml;
    }
}

async function deleteProduto(id) {
    const result = await makeRequest(`${API_BASE_URL}/produtos/${id}`, { method: 'DELETE' });
    if (result) {
        showMessage('Produto excluído com sucesso!', 'success');
        listProdutos();
    }
}

// FORNECEDOR functions
async function createFornecedor(event) {
    event.preventDefault();
    const nome = document.getElementById('fornecedor-nome').value;
    const contato = document.getElementById('fornecedor-contato').value;
    const endereco = document.getElementById('fornecedor-endereco').value;

    const payload = { nome, contato, endereco };
    const fornecedor = await makeRequest(`${API_BASE_URL}/fornecedores`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });
    if (fornecedor) {
        showMessage('Fornecedor criado com sucesso!', 'success');
        listFornecedores();
        document.getElementById('form-fornecedor-create').reset();
    }
}

async function listFornecedores() {
    const fornecedores = await makeRequest(`${API_BASE_URL}/fornecedores`, { method: 'GET' });
    const outputDiv = document.getElementById('output-fornecedor-list');
    if (fornecedores && outputDiv) {
        let tableHtml = '<table><thead><tr><th>ID</th><th>Nome</th><th>Contato</th><th>Endereço</th><th>Ações</th></tr></thead><tbody>';
        fornecedores.forEach(fornecedor => {
            tableHtml += `
                <tr>
                    <td>${fornecedor.id}</td>
                    <td>${fornecedor.nome}</td>
                    <td>${fornecedor.contato}</td>
                    <td>${fornecedor.endereco}</td>
                    <td>
                        <button onclick="editFornecedor(${fornecedor.id})">Editar</button>
                        <button class="delete-button" onclick="deleteFornecedor(${fornecedor.id})">Excluir</button>
                    </td>
                </tr>
            `;
        });
        tableHtml += '</tbody></table>';
        outputDiv.innerHTML = tableHtml;
    }
}

async function deleteFornecedor(id) {
    const result = await makeRequest(`${API_BASE_URL}/fornecedores/${id}`, { method: 'DELETE' });
    if (result) {
        showMessage('Fornecedor excluído com sucesso!', 'success');
        listFornecedores();
    }
}

// CLIENTE functions
async function createCliente(event) {
    event.preventDefault();
    const nome = document.getElementById('cliente-nome').value;
    const cpf = document.getElementById('cliente-cpf').value;
    const email = document.getElementById('cliente-email').value;
    const telefone = document.getElementById('cliente-telefone').value;

    const payload = { nome, cpf, email, telefone };
    const cliente = await makeRequest(`${API_BASE_URL}/clientes`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });
    if (cliente) {
        showMessage('Cliente criado com sucesso!', 'success');
        listClientes();
        document.getElementById('form-cliente-create').reset();
    }
}

async function listClientes() {
    const clientes = await makeRequest(`${API_BASE_URL}/clientes`, { method: 'GET' });
    const outputDiv = document.getElementById('output-cliente-list');
    if (clientes && outputDiv) {
        let tableHtml = '<table><thead><tr><th>ID</th><th>Nome</th><th>CPF</th><th>Email</th><th>Telefone</th><th>Ações</th></tr></thead><tbody>';
        clientes.forEach(cliente => {
            tableHtml += `
                <tr>
                    <td>${cliente.id}</td>
                    <td>${cliente.nome}</td>
                    <td>${cliente.cpf}</td>
                    <td>${cliente.email}</td>
                    <td>${cliente.telefone}</td>
                    <td>
                        <button onclick="editCliente(${cliente.id})">Editar</button>
                        <button class="delete-button" onclick="deleteCliente(${cliente.id})">Excluir</button>
                    </td>
                </tr>
            `;
        });
        tableHtml += '</tbody></table>';
        outputDiv.innerHTML = tableHtml;
    }
}

async function deleteCliente(id) {
    const result = await makeRequest(`${API_BASE_URL}/clientes/${id}`, { method: 'DELETE' });
    if (result) {
        showMessage('Cliente excluído com sucesso!', 'success');
        listClientes();
    }
}

async function editCliente(id) {
    const cliente = await makeRequest(`${API_BASE_URL}/clientes/${id}`, { method: 'GET' });
    if (cliente) {
        document.getElementById('edit-client-form-container').style.display = 'flex';
        document.getElementById('edit-cliente-id').value = cliente.id;
        document.getElementById('edit-cliente-nome').value = cliente.nome;
        document.getElementById('edit-cliente-cpf').value = cliente.cpf;
        document.getElementById('edit-cliente-email').value = cliente.email;
        document.getElementById('edit-cliente-telefone').value = cliente.telefone;
    }
}

function cancelEdit() {
    document.getElementById('edit-client-form-container').style.display = 'none';
}

async function updateCliente(event) {
    event.preventDefault();
    const id = document.getElementById('edit-cliente-id').value;
    const payload = {
        id: id,
        nome: document.getElementById('edit-cliente-nome').value,
        cpf: document.getElementById('edit-cliente-cpf').value,
        email: document.getElementById('edit-cliente-email').value,
        telefone: document.getElementById('edit-cliente-telefone').value,
    };
    const updatedClient = await makeRequest(`${API_BASE_URL}/clientes/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });
    if (updatedClient) {
        showMessage('Cliente atualizado com sucesso!', 'success');
        cancelEdit();
        listClientes();
    }
}

// VENDA functions
function setupSearch(inputId, hiddenInputId, resultsId, entity, isProduct = false) {
    const searchInput = document.getElementById(inputId);
    const hiddenInput = document.getElementById(hiddenInputId);
    const resultsList = document.getElementById(resultsId);

    if (searchInput && hiddenInput && resultsList) {
        searchInput.addEventListener('input', async (e) => {
            const query = e.target.value;
            if (query.length > 2) {
                const data = await makeRequest(`${API_BASE_URL}/${entity}/search?nome=${encodeURIComponent(query)}`, { method: 'GET' });
                displaySearchResults(data, resultsList, hiddenInput, searchInput, isProduct);
            } else {
                resultsList.style.display = 'none';
            }
        });

        searchInput.addEventListener('focus', () => {
            if (searchInput.value.length > 2) {
                resultsList.style.display = 'block';
            }
        });

        document.addEventListener('click', (e) => {
            if (!e.target.closest('.search-container')) {
                resultsList.style.display = 'none';
            }
        });
    }
}

function displaySearchResults(data, resultsList, hiddenInput, searchInput, isProduct) {
    if (!resultsList) return;
    resultsList.innerHTML = '';
    resultsList.style.display = 'block';

    if (!data || data.length === 0) {
        resultsList.innerHTML = '<li>Nenhum resultado encontrado</li>';
        return;
    }

    data.forEach(item => {
        const li = document.createElement('li');
        li.textContent = item.nome;
        li.onclick = () => {
            searchInput.value = item.nome;
            hiddenInput.value = item.id;
            resultsList.style.display = 'none';
            if (isProduct) {
                document.getElementById('venda-produto-preco-unitario').value = item.valorDeVenda;
            }
        };
        resultsList.appendChild(li);
    });
}

async function adicionarItem() {
    const produtoId = document.getElementById('venda-produto-id').value;
    const produtoNome = document.getElementById('produto-search-input').value;
    const quantidade = parseInt(document.getElementById('venda-quantidade').value);
    const precoUnitario = parseFloat(document.getElementById('venda-produto-preco-unitario').value);

    if (!produtoId || quantidade <= 0 || isNaN(quantidade) || !precoUnitario || isNaN(precoUnitario)) {
        showMessage('Por favor, selecione um produto e insira uma quantidade válida.', 'error');
        return;
    }

    // Validação de estoque no front-end
    const produto = await makeRequest(`${API_BASE_URL}/produtos/${produtoId}`, { method: 'GET' });
    if (!produto || produto.quantidade < quantidade) {
        showMessage('Quantidade insuficiente em estoque.', 'error');
        return;
    }

    const itemExistente = itensVenda.find(item => item.produtoId === produtoId);
    if (itemExistente) {
        // Valida se a quantidade total (existente + nova) não excede o estoque
        if ((itemExistente.quantidade + quantidade) > produto.quantidade) {
            showMessage(`A quantidade total do produto '${produto.nome}' excede o estoque disponível (${produto.quantidade}).`, 'error');
            return;
        }
        itemExistente.quantidade += quantidade;
    } else {
        const novoItem = {
            produtoId,
            produtoNome,
            quantidade,
            precoUnitario
        };
        itensVenda.push(novoItem);
    }

    renderizarItensVenda();
    limparFormularioItem();
}

function removerItem(produtoId) {
    itensVenda = itensVenda.filter(item => item.produtoId !== produtoId);
    renderizarItensVenda();
}

function renderizarItensVenda() {
    const listaItens = document.getElementById('itens-venda-list');
    if (!listaItens) return;
    listaItens.innerHTML = '';
    let totalVenda = 0;
    let totalItens = 0;

    itensVenda.forEach(item => {
        const subtotal = item.quantidade * item.precoUnitario;
        totalVenda += subtotal;
        totalItens += item.quantidade;

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${item.produtoNome}</td>
            <td>${item.quantidade}</td>
            <td style="width: 120px;"><div style="display: flex; justify-content: space-between; align-items: center; padding: 0 5px;"><span>R$</span><span>${formatCurrencyBRL(item.precoUnitario)}</span></div></td>
            <td style="width: 120px;"><div style="display: flex; justify-content: space-between; align-items: center; padding: 0 5px;"><span>R$</span><span>${formatCurrencyBRL(subtotal)}</span></div></td>
            <td><button onclick="removerItem('${item.produtoId}')">Remover</button></td>
        `;
        listaItens.appendChild(tr);
    });

    const totalElement = document.getElementById('venda-total');
    if (totalElement) {
        totalElement.textContent = formatCurrencyBRL(totalVenda);
    }

    const totalItensElement = document.getElementById('venda-total-itens');
    if (totalItensElement) {
        totalItensElement.textContent = totalItens;
    }
}

function limparFormularioItem() {
    const produtoSearch = document.getElementById('produto-search-input');
    const produtoId = document.getElementById('venda-produto-id');
    const quantidade = document.getElementById('venda-quantidade');
    const precoUnitario = document.getElementById('venda-produto-preco-unitario');
    const searchResults = document.getElementById('produto-search-results');

    if (produtoSearch) produtoSearch.value = '';
    if (produtoId) produtoId.value = '';
    if (quantidade) quantidade.value = '';
    if (precoUnitario) precoUnitario.value = '';
    if (searchResults) searchResults.style.display = 'none';
}

async function listVendas() {
    const vendas = await makeRequest(`${API_BASE_URL}/vendas`, { method: 'GET' });
    const outputDiv = document.getElementById('output-venda-list');

    if (vendas && outputDiv) {
        let tableHtml = '<table><thead><tr><th>ID</th><th>Cliente</th><th>Data</th><th style="text-align: center; width: 120px;">Valor Total</th><th>Ações</th></tr></thead><tbody>';
        vendas.forEach(venda => {
            const clienteNome = venda.cliente ? venda.cliente.nome : 'N/A';
            const valorTotalVenda = venda.valorTotal !== null ? venda.valorTotal : 0;
            const dataVenda = venda.dataVenda ? new Date(venda.dataVenda).toLocaleDateString('pt-BR') : 'N/A';

            tableHtml += `
                <tr id="venda-row-${venda.id}" onclick="toggleDetalhes(${venda.id})" style="cursor: pointer;">
                    <td>${venda.id}</td>
                    <td>${clienteNome}</td>
                    <td>${dataVenda}</td>
                    <td><div style="display: flex; justify-content: space-between; align-items: center; padding: 0 5px;"><span>R$</span><span>${formatCurrencyBRL(valorTotalVenda)}</span></div></td>
                    <td>
                        <div class="action-buttons">
                            <button class="delete-button" onclick="event.stopPropagation(); deleteVenda(${venda.id})">Excluir</button>
                        </div>
                    </td>
                </tr>
                <tr id="detalhes-venda-${venda.id}" style="display:none;">
                    <td colspan="5">
                        <div class="detalhes-container">
                            <h4>Itens da Venda</h4>
                            <table class="itens-detalhe-table">
                                <thead>
                                    <tr>
                                        <th>Produto</th>
                                        <th>Qtd</th>
                                        <th style="text-align: center; width: 120px;">Preço Unitário</th>
                                        <th style="text-align: center; width: 120px;">Subtotal</th>
                                    </tr>
                                </thead>
                                <tbody>`;

            if (venda.itensVenda) {
                let totalItensNaVenda = 0;
                let totalValorNaVenda = 0;

                venda.itensVenda.forEach(item => {
                    const preco = item.precoUnitario || 0;
                    const subtotal = item.quantidade * preco;
                    totalItensNaVenda += item.quantidade;
                    totalValorNaVenda += subtotal;

                    tableHtml += `
                                    <tr>
                                        <td>${item.produto.nome}</td>
                                        <td>${item.quantidade}</td>
                                        <td><div style="display: flex; justify-content: space-between; align-items: center; padding: 0 5px;"><span>R$</span><span>${formatCurrencyBRL(preco)}</span></div></td>
                                        <td><div style="display: flex; justify-content: space-between; align-items: center; padding: 0 5px;"><span>R$</span><span>${formatCurrencyBRL(subtotal)}</span></div></td>
                                    </tr>`;
                });

                tableHtml += `
                                </tbody>
                                <tfoot>
                                    <tr>
                                        <td colspan="2"><strong>Total de Itens:</strong> ${totalItensNaVenda}</td>
                                        <td style="text-align: right;" colspan="2"><strong>Total da Venda:</strong> R$ ${formatCurrencyBRL(totalValorNaVenda)}</td>
                                    </tr>
                                </tfoot>
                            </table>
                        </div>
                    </td>
                </tr>`;
            } else {
                 tableHtml += `
                                </tbody>
                                <tfoot>
                                    <tr>
                                        <td colspan="4">Nenhum item encontrado para esta venda.</td>
                                    </tr>
                                </tfoot>
                            </table>
                        </div>
                    </td>
                </tr>`;
            }
        });
        tableHtml += '</tbody></table>';
        outputDiv.innerHTML = tableHtml;
    }
}

function toggleDetalhes(id) {
    const row = document.getElementById(`detalhes-venda-${id}`);
    const mainRow = document.getElementById(`venda-row-${id}`);

    const allRows = document.querySelectorAll('#output-venda-list table tr');
    allRows.forEach(r => r.classList.remove('venda-selecionada'));

    if (row && mainRow) {
        if (row.style.display === 'none') {
            row.style.display = 'table-row';
            mainRow.classList.add('venda-selecionada');
            row.classList.add('venda-selecionada');
        } else {
            row.style.display = 'none';
        }
    }
}

async function deleteVenda(id) {
    const result = await makeRequest(`${API_BASE_URL}/vendas/${id}`, { method: 'DELETE' });
    if (result !== null) {
        showMessage('Venda excluída com sucesso!', 'success');
        listVendas();
    }
}

async function registrarVenda(event) {
    event.preventDefault();

    const clienteId = document.getElementById('venda-cliente-id').value;
    if (!clienteId) {
        showMessage('Por favor, selecione um cliente.', 'error');
        return;
    }

    if (itensVenda.length === 0) {
        showMessage('Por favor, adicione pelo menos um produto à venda.', 'error');
        return;
    }

    const itensDTO = itensVenda.map(item => ({
        produtoId: item.produtoId,
        quantidade: item.quantidade
    }));

    const payload = {
        clienteId: clienteId,
        itensVenda: itensDTO
    };

    const venda = await makeRequest(`${API_BASE_URL}/vendas`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });
    if (venda) {
        showMessage('Venda registrada com sucesso!', 'success');
        listVendas();
        document.getElementById('form-venda-create').reset();
        document.getElementById('cliente-search-input').value = '';
        document.getElementById('venda-cliente-id').value = '';
        itensVenda = [];
        renderizarItensVenda();
    }
}