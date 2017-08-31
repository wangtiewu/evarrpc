package com.eastelsoft.etos2.rpc;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinPool;

public class RpcRequestExecutorAkka implements RpcRequestExecutor {
	private final static Logger logger = LoggerFactory
			.getLogger(RpcRequestExecutorAkka.class);
	private ActorSystem system = null;
	private ActorRef actor = null;

	public RpcRequestExecutorAkka(int workerCount) {
		// TODO Auto-generated constructor stub
		system = ActorSystem.create("RpcRequestExecutorAkka");
		actor = system
				.actorOf(
						Props.create(HandleAkkaActor.class, this).withRouter(
								new RoundRobinPool(workerCount)),
						"handleAkkaActor");
		logger.info("akka router: 策略：RoundRobin，数量：" + workerCount);
	}

	@Override
	public void submit(RpcRequestTask rpcInboundTask) {
		// TODO Auto-generated method stub
		actor.tell(rpcInboundTask, null);
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		if (system != null && actor != null) {
			system.stop(actor);
			actor = null;
		}
		system.shutdown();
	}

	class HandleAkkaActor extends UntypedActor {

		@Override
		public void onReceive(Object arg0) throws Exception {
			// TODO Auto-generated method stub
			if (arg0 instanceof RpcRequestTask) {
				((RpcRequestTask) arg0).run();
			} else {
				unhandled(arg0);
			}
		}

	}
}
