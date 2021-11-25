<template>
  <div class="loading_content">
<!--    TODO maybe the slot could be showed when loading for better UX -->
    <slot v-if="status === 'ready'"/>
    <div class="loading" v-if="status === 'loading'">Загрузка...</div>
    <div class="error" v-if="status === 'error'">Сервер отвалился :(</div>
    <div class="error" v-if="status === 'offline'">Вы не подключены к интернету &#129430;</div>
    <btn black mdium thick v-if="status === 'error' || status === 'offline'" @click="$emit('reload')">Обновить</btn>
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

<style scoped>
  div {
    font-size: 24px;
  }

  .loading {
    animation: loading_anim 1s ease-in-out infinite;
  }

  .error {
    margin-bottom: 25px;
  }

  @keyframes loading_anim {
    0%   { opacity: 0.1; }
    50%  { opacity: 1.0; }
    100% { opacity: 0.1; }
  }
</style>
