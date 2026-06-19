CREATE SCHEMA IF NOT EXISTS boi_na_brasa DEFAULT CHARACTER SET utf8mb4;
USE boi_na_brasa;

CREATE TABLE IF NOT EXISTS Pessoa (
    id       INT          NOT NULL AUTO_INCREMENT,
    nome     VARCHAR(100) NOT NULL,
    email    VARCHAR(100) NULL,
    cpf_cnpj VARCHAR(20)  NOT NULL,
    tipo     ENUM('PF', 'PJ') NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (cpf_cnpj)
);

CREATE TABLE IF NOT EXISTS Produto (
    id                INT           NOT NULL AUTO_INCREMENT,
    descricao         VARCHAR(100)  NOT NULL,
    valor             DECIMAL(10,2) NOT NULL,
    unidade           VARCHAR(20)   NOT NULL,
    quantidadeEstoque INT           NOT NULL DEFAULT 0,
    controleEstoque   BOOLEAN       NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS Compra (
    id               INT           NOT NULL AUTO_INCREMENT,
    data             DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valorTotal       DECIMAL(10,2) NOT NULL,
    idPessoaJuridica INT           NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (idPessoaJuridica) REFERENCES Pessoa(id)
);

CREATE TABLE IF NOT EXISTS CompraProduto (
    id         INT           NOT NULL AUTO_INCREMENT,
    quantidade INT           NOT NULL,
    valor      DECIMAL(10,2) NOT NULL,
    idCompra   INT           NOT NULL,
    idProduto  INT           NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (idCompra)  REFERENCES Compra(id),
    FOREIGN KEY (idProduto) REFERENCES Produto(id)
);

CREATE TABLE IF NOT EXISTS Venda (
    id             INT           NOT NULL AUTO_INCREMENT,
    data           DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valorTotal     DECIMAL(10,2) NOT NULL,
    idPessoaFisica INT           NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (idPessoaFisica) REFERENCES Pessoa(id)
);

CREATE TABLE IF NOT EXISTS VendaProduto (
    id         INT           NOT NULL AUTO_INCREMENT,
    quantidade INT           NOT NULL,
    valor      DECIMAL(10,2) NOT NULL,
    idVenda    INT           NOT NULL,
    idProduto  INT           NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (idVenda)   REFERENCES Venda(id),
    FOREIGN KEY (idProduto) REFERENCES Produto(id)
);