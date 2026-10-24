# Integração do King Live com TikTok LIVE

## Estado atual

A interface Android do King Live foi melhorada. Os botões de comentários e presentes continuam sendo **eventos simulados**. Nenhum evento real do TikTok é recebido nesta versão.

## Caminhos possíveis

### 1. API oficial do TikTok

A documentação pública do TikTok for Developers inclui APIs e eventos para casos de uso específicos, mas a documentação consultada não confirma uma API pública geral para receber em tempo real todos os comentários e presentes de qualquer LIVE. Não devemos tratar Webhooks de publicação/autorização como se fossem eventos de presentes LIVE. Antes de escolher esse caminho, é necessário confirmar elegibilidade, permissões e endpoints com o TikTok.

Documentação: https://developers.tiktok.com/docs/en/webhooks-events

### 2. Ponte de eventos comunitária (não oficial)

Há projetos comunitários, como TikTokLive para Python, que tentam receber eventos públicos de LIVE, incluindo comentários e presentes:
https://github.com/kendykim/TikTokLive

**Atenção:** esta não é uma integração oficial do TikTok. Pode parar de funcionar se o TikTok alterar seus protocolos, depender de serviços externos e estar sujeita aos termos e limites aplicáveis. Não há garantia de disponibilidade.

## Arquitetura recomendada para um protótipo

1. Um serviço Python separado conecta-se à LIVE usando o nome público do criador.
2. O serviço normaliza os eventos recebidos e disponibiliza-os ao King Live por HTTPS.
3. O APK consulta o serviço e atualiza o placar.
4. Comentários podem pontuar conforme regras configuráveis; presentes podem pontuar conforme quantidade ou valor informado pelo evento.
5. O app deve deduplicar eventos e tratar sequências de presentes para evitar pontuação repetida.

## Segurança

- Nunca colocar cookies de sessão, tokens privados ou chaves de API dentro do APK ou no repositório.
- Não enviar credenciais da conta TikTok para serviços de terceiros.
- Usar HTTPS e autenticação entre o APK e o serviço.
- Exibir claramente quando a conexão estiver desligada ou quando os eventos forem apenas de teste.
- Respeitar os termos do TikTok e as regras de privacidade aplicáveis.

## Próximas etapas técnicas

- Escolher e validar o método de recepção de eventos.
- Criar o serviço de ponte com reconexão e endpoint de saúde.
- Definir as regras de pontuação para comentários e presentes.
- Adicionar ao APK uma tela de configuração da URL do serviço, status de conexão e feed de eventos.
- Compilar e testar o APK e a ponte antes de afirmar que a integração real funciona.

## Regras de pontuação ainda a definir

O placar atual usa +1 e +5 apenas para demonstração. A versão real deve permitir escolher se cada comentário vale ponto, se apenas palavras/comandos específicos pontuam, e como os presentes são convertidos em pontos.
