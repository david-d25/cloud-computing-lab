<template>
  <div class="container"><slot/></div>
</template>

<script>
export default {}
</script>

<style scoped>
.container {
  max-width: 100%;
  width: 1200px;
  margin: auto;
}
@media (max-width: 1366px) {
  .container {
    width: 1000px;
  }
}
@media (max-width: 1080px) {
  .container {
    width: 800px;
  }
}
@media (max-width: 900px) {
  .container {
    width: 700px;
  }
}
/* Tablet */
@media (max-width: 768px) {
  .container {
    width: 600px;
  }
}
@media (max-width: 640px) {
  .container {
    width: 400px;
  }
}
@media (max-width: 480px) {
  .container {
    width: calc(100% - 40px);
  }
}
@media (max-width: 320px) {
  .container {
    width: 100%;
  }
}
</style>
