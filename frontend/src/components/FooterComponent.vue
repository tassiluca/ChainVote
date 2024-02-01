<template>
  <div id="footer" class="container-fluid bg-primary">
    <div class="row">
      <div class="col-md-12 center mx-auto">
        <strong>ChainVote</strong>
        <p>DS + ASW Final Project @ UniBo</p>
      </div>
    </div>
    <div class="row">
      <div class="col">
        <strong>Authors</strong>
        <ul>
          <li><a href="https://github.com/giovaz94">Giovanni Antonioni</a></li>
          <li><a href="https://github.com/tassiLuca">Luca Tassinari</a></li>
          <li><a href="https://github.com/Luca1313">Luca Rubboli</a></li>
        </ul>
      </div>
      <div class="col">
        <strong>Useful links</strong>
        <ul>
          <!-- TODO: to adjust -->
          <li><a href="/">Homepage</a></li>
          <li><a href="/user">User Area</a></li>
        </ul>
      </div>
    </div>
    <div class="row">
      <hr />
      <p>Made with Love 🤍 &#x2022; <a href="https://github.com/tassiLuca/ChainVote" target="_blank">GitHub project</a></p>
    </div>
  </div>
</template>

<script setup lang="ts"></script>

<style scoped>
.row {
  margin: 0;
}

#footer {
  color: white;
  text-align: center;
  padding: 20px 0;
  border-radius: 15px 15px 0 0;
  box-shadow: 1px -3px 10px rgba(200, 200, 200, 0.82);
}

#footer ul {
  list-style-type: none;
  padding: 0;
}

#footer a {
  color: white;
}

#footer a:hover {
  color: white;
  opacity: 0.7;
  transition: 0.5s;
}

#footer hr {
  border-top: 1px solid white;
  width: 80%;
  margin: 10px auto;
}
</style>
