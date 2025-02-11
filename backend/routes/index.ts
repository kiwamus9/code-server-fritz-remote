import express from 'express'
const router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'kiwamu' });
});

router.get('/2', function(req, res, next) {
  res.render('index2', { title: 'kiwamu' });
});


export default router;