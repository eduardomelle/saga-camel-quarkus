package br.com.eduardomelle;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.SagaPropagation;
import org.apache.camel.saga.CamelSagaService;
import org.apache.camel.saga.InMemorySagaService;

@ApplicationScoped
public class SagaRoute extends RouteBuilder {

  @Inject
  PedidoService pedidoService;

  @Inject
  CreditoService creditoService;

  @Override
  public void configure() throws Exception {
    CamelSagaService sagaService = new InMemorySagaService();
    getContext().addService(sagaService);

    // Saga:
    from("direct:saga").saga().propagation(SagaPropagation.REQUIRES_NEW).log("Iniciando a transação")
        .to("direct:newPedido").log("Criando novo pedido")
        .to("direct:newPedidoValor").log("Reservando o crédito")
        .to("direct:finaliza").log("Feito!");

    // Pedido Service:
    from("direct:newPedido").saga().propagation(SagaPropagation.MANDATORY)
        .compensation("direct:cancelPedido")
        .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
        .bean(pedidoService, "newPedido").log("Pedido $<body> criado");

    from("direct:cancelPedido")
        .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
        .bean(pedidoService, "cancelPedido").log("Pedido $<body> cancelado");

    // Crédito Service:

  }

}
