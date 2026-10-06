# Jogo dos Robôs (JavaFX)

Rodar: `mvn javafx:run`

## Estrutura
- `classes_robos`, `classes_obstaculos`, `excecoes` — modelo original (não alterado).
- `tabuleiro/Tabuleiro` — modelo do tabuleiro 4x4 (alimento escolhido, obstáculos sorteados).
- `modos/` — lógica dos 4 modos (equivalem às 4 Mains), sem JavaFX.
- `visual/` — sprites, animações e desenho do tabuleiro.
- `interfaces/` — controllers (telas) e navegação.
- `resources/fxml`, `resources/css`, `resources/sprites`.

## Fluxo
App → TelaInicial → Configuracao (varia por modo) → Jogo
