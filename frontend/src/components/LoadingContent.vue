<template>
  <div class="loading_content">
    <div class="content" :class="{ active: status === 'ready', loading: status === 'loading' }">
      <slot class="slot" />
    </div>
    <div class="overlay" v-if="status !== 'ready'">
      <div class="loading" v-if="status === 'loading'">Загрузка...</div>
      <div class="error" v-if="status === 'error'">Сервер отвалился :(</div>
      <div class="error" v-if="status === 'forbidden'">Сюда доступ закрыт &#x1F5FF;</div>
      <div class="error" v-if="status === 'offline'">Интернет отвалился &#129430;</div>
      <btn black mdium thick v-if="status === 'error' || status === 'offline' || status === 'forbidden'" @click="$emit('reload')">Ещё раз</btn>
    </div>
  </div>
</template>

<script>
import Btn from "#/components/Btn";
export default {
  name: "LoadingContent",
  components: { Btn },
  props: ['status']
}
</script>

<style scoped lang="scss">
  div {
    font-size: 24px;
  }

  .overlay {
    .loading {
      animation: loading_anim 1s ease-in-out infinite;
    }

    .error {
      margin-bottom: 25px;
    }
  }

  .loading_content {
    position: relative;
  }

  .overlay {
    position: absolute;
    width: 100%;
    top: 50%;
    left: 50%;
    margin: auto;
    transform: translate(-50%, -50%);
  }

  .content {
    transition: var(--default-transition), min-height 300ms ease-in-out;
    pointer-events: none;
    min-height: 130px;
    opacity: 0;

    &.loading {
      opacity: .25;
      min-height: 50px;
    }

    &.active {
      opacity: 1;
      min-height: 0;
      pointer-events: auto;
    }
  }

  .loading_content[hide-on-loading] .content .loading {
    opacity: 0;
  }

  @keyframes loading_anim {
    0%   { opacity: 0.1; }
    50%  { opacity: 1.0; }
    100% { opacity: 0.1; }
  }
</style>
