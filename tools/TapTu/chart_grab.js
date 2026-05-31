// run this in https://vibe.naver.com/chart
const cleanText = (text) => (text || '').replace(/\u00a0/g, ' ').replace(/\s+/g, ' ').trim();

const top100Section = document.querySelector('.track_section');
const trs = top100Section.querySelectorAll('tr')
const songs = []
for (const tr of trs) {
  const rankText = cleanText(tr.querySelector('.rank span.text')?.textContent);
  const rank = parseInt(rankText, 10);
  if (isNaN(rank) || rank > 20) continue;
  const thumbnail = tr.querySelector('.thumb .inner img')?.src || '';
  const title = cleanText(tr.querySelector('.inner_cell a.link_text span')?.textContent);
  const artist = cleanText(tr.querySelector('.artist .link_artist .text')?.textContent);
  const album = cleanText(tr.querySelector('.album .link')?.textContent);
  const song = {
    rank, title, artist, album, thumbnail,
  }
  songs.push(song)
}

console.log(songs)
console.log(JSON.stringify(songs, null, 2))
