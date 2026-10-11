# Candidaturas

App Android para acompanhar as vagas em que você se candidatou: registre empresa, vaga e link, mude o status
(enviada, entrevista, oferta, recusada), busque e filtre. Funciona offline, sem conta e sem enviar dados para fora do aparelho.

Feito com **Kotlin** e **Jetpack Compose (Material 3)**, com tema claro/escuro e cores dinâmicas no Android 12+.

## Funcionalidades

- Cadastro de candidatura com empresa, vaga e link opcional (abre a vaga no navegador).
- Status com um toque: Enviada, Entrevista, Oferta, Recusada.
- Filtros por status, com contador, e busca por empresa ou vaga.
- Dados salvos no aparelho (JSON em `SharedPreferences`).

## Como está organizado

```
app/src/main/java/dev/flayksdev/candidaturas/
├── data/    JobApplication.kt (modelo e regras de filtro/contagem), ApplicationRepository.kt (persistência)
├── ui/      ApplicationsViewModel.kt (estado), ApplicationsScreen.kt (telas em Compose), theme/
└── MainActivity.kt
app/src/test/                      testes unitários das regras de filtro e contagem
.github/workflows/android.yml      CI: roda os testes e compila o APK a cada push
```

Decisões de projeto:
- **Regras fora da interface.** Filtro, busca e contagem são funções puras em `data/`, testáveis na JVM sem emulador.
- **Estado em um só lugar.** O `ViewModel` expõe um `StateFlow` e grava a cada mudança, então girar a tela ou fechar o app não perde dados.
- **Sem dependências extras.** Só AndroidX e Compose, o que mantém o APK pequeno e a build simples.

## Como rodar

1. Instale o Android Studio (versão recente) e abra esta pasta.
2. Aguarde o sync do Gradle e execute no emulador ou em um aparelho (Android 8.0+).

Pela linha de comando, com o Android SDK instalado e o Gradle 8.9 ou superior:

```bash
gradle testDebugUnitTest   # testes
gradle assembleDebug       # gera app/build/outputs/apk/debug/app-debug.apk
```

## CI

A cada push o GitHub Actions executa os testes unitários e compila o APK de debug, que fica disponível como
artefato da execução, na aba **Actions**.

## Próximos passos

- Lembretes para acompanhar vagas sem resposta depois de alguns dias.
- Exportar e importar a lista em CSV.
- Trocar o armazenamento por Room, caso a lista cresça.
