// run this in https://vibe.naver.com/chart
const top100Section = document.querySelector('.track_section');
const trs = top100Section.querySelectorAll('tr')
const songs = []
for (const tr of trs) {
  const rankText = tr.querySelector('.rank span.text')?.textContent.trim();
  const rank = parseInt(rankText, 10);
  if (isNaN(rank) || rank > 20) continue;
  const thumbnail = tr.querySelector('.thumb .inner img')?.src || '';
  const title = tr.querySelector('.inner_cell a.link_text span')?.textContent.trim() || '';
  const artist = tr.querySelector('.artist .link_artist .text')?.textContent.trim() || '';
  const album = tr.querySelector('.album .link')?.textContent.trim() || '';
  const song = {
    rank, title, artist, album, thumbnail,
  }
  songs.push(song)
}

console.log(songs)
console.log(JSON.stringify(songs, null, 2))