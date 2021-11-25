<template>
  <div class="logo">
    <h1>Божественный текстогенератор</h1>
    <div class="left">by Илья и Давид</div>
  </div>
</template>

<script>
export default {
  name: "Logo"
}
</script>

<style scoped lang="scss">
  .logo {
    text-align: left;
    margin: 0 50px 69px 51px;
  }

  h1 {
    font-size: 36px;
    text-transform: uppercase;
    position: relative;
    margin: 0;

    &:after {
      display: block;
      content: '18+';
      position: absolute;
      right: 0;
      color: grey;
      font-weight: normal;
      bottom: -1em;
    }
  }

  .left {
    display: inline;
    color: grey;
  }

  @media (max-width: 400px) {
    .logo {
      margin: 0 0 69px 0;
    }

    h1 {
      font-size: 32px;
    }
  }
</style>
